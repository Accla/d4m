/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

#include <cppunit/extensions/HelperMacros.h>
#include "CppAssertHelper.h"

#include <stdlib.h>
#include <sys/select.h>

#include "CollectionUtil.h"
#include "ThreadingUtil.h"

using namespace Util;

#include "Vector.h"
using namespace std;

#include <cstring>
#include <list>

#include <zookeeper.h>

#ifdef THREADED
    static void yield(zhandle_t *zh, int i)
    {
        sleep(i);
    }
#else
    static void yield(zhandle_t *zh, int seconds)
    {
        int fd;
        int interest;
        int events;
        struct timeval tv;
        int rc;
        time_t expires = time(0) + seconds; 
        time_t timeLeft = seconds;
        fd_set rfds, wfds, efds;
        FD_ZERO(&rfds);
        FD_ZERO(&wfds);
        FD_ZERO(&efds);

        while(timeLeft >= 0) {
            zookeeper_interest(zh, &fd, &interest, &tv);
            if (fd != -1) {
                if (interest&ZOOKEEPER_READ) {
                    FD_SET(fd, &rfds);
                } else {
                    FD_CLR(fd, &rfds);
                }
                if (interest&ZOOKEEPER_WRITE) {
                    FD_SET(fd, &wfds);
                } else {
                    FD_CLR(fd, &wfds);
                }
            } else {
                fd = 0;
            }
            FD_SET(0, &rfds);
            if (tv.tv_sec > timeLeft) {
                tv.tv_sec = timeLeft;
            }
            rc = select(fd+1, &rfds, &wfds, &efds, &tv);
            timeLeft = expires - time(0);
            events = 0;
            if (FD_ISSET(fd, &rfds)) {
                events |= ZOOKEEPER_READ;
            }
            if (FD_ISSET(fd, &wfds)) {
                events |= ZOOKEEPER_WRITE;
            }
            zookeeper_process(zh, events);
        }
    }
#endif

typedef struct evt {
    string path;
    int type;
} evt_t;

typedef struct watchCtx {
private:
    list<evt_t> events;
    watchCtx(const watchCtx&);
    watchCtx& operator=(const watchCtx&);
public:
    bool connected;
    zhandle_t *zh;
    Mutex mutex;

    watchCtx() {
        connected = false;
        zh = 0;
    }
    ~watchCtx() {
        if (zh) {
            zookeeper_close(zh);
            zh = 0;
        }
    }

    evt_t getEvent() {
        evt_t evt;
        mutex.acquire();
        CPPUNIT_ASSERT( events.size() > 0);
        evt = events.front();
        events.pop_front();
        mutex.release();
        return evt;
    }

    int countEvents() {
        int count;
        mutex.acquire();
        count = events.size();
        mutex.release();
        return count;
    }

    void putEvent(evt_t evt) {
        mutex.acquire();
        events.push_back(evt);
        mutex.release();
    }

    bool waitForConnected(zhandle_t *zh) {
        time_t expires = time(0) + 10;
        while(!connected && time(0) < expires) {
            yield(zh, 1);
        }
        return connected;
    }
    bool waitForDisconnected(zhandle_t *zh) {
        time_t expires = time(0) + 15;
        while(connected && time(0) < expires) {
            yield(zh, 1);
        }
        return !connected;
    }
} watchctx_t; 

class Zookeeper_simpleSystem : public CPPUNIT_NS::TestFixture
{
    CPPUNIT_TEST_SUITE(Zookeeper_simpleSystem);
    CPPUNIT_TEST(testAsyncWatcherAutoReset);
#ifdef THREADED
    CPPUNIT_TEST(testNullData);
    CPPUNIT_TEST(testPathValidation);
    CPPUNIT_TEST(testPing);
    CPPUNIT_TEST(testAcl);
    CPPUNIT_TEST(testChroot);
    CPPUNIT_TEST(testAuth);
    CPPUNIT_TEST(testWatcherAutoResetWithGlobal);
    CPPUNIT_TEST(testWatcherAutoResetWithLocal);
#endif
    CPPUNIT_TEST_SUITE_END();

    static void watcher(zhandle_t *, int type, int state, const char *path,void*v){
        watchctx_t *ctx = (watchctx_t*)v;

        if (state == ZOO_CONNECTED_STATE) {
            ctx->connected = true;
        } else {
            ctx->connected = false;
        }
        if (type != ZOO_SESSION_EVENT) {
            evt_t evt;
            evt.path = path;
            evt.type = type;
            ctx->putEvent(evt);
        }
    }

    static const char hostPorts[];

    const char *getHostPorts() {
        return hostPorts;
    }
    
    zhandle_t *createClient(watchctx_t *ctx) {
        zhandle_t *zk = zookeeper_init(hostPorts, watcher, 10000, 0,
                                       ctx, 0);
        ctx->zh = zk;
        sleep(1);
        return zk;
    }
    
    zhandle_t *createchClient(watchctx_t *ctx) {
        zhandle_t *zk = zookeeper_init(hp_chroot, watcher, 10000, 0,
                                       ctx, 0);
        ctx->zh = zk;
        sleep(1);
        return zk;
    }
        
public:


    void setUp()
    {
        char cmd[1024];
        sprintf(cmd, "%s startClean %s", ZKSERVER_CMD, getHostPorts());
        CPPUNIT_ASSERT(system(cmd) == 0);
    }
    

    void startServer() {
        char cmd[1024];
        sprintf(cmd, "%s start %s", ZKSERVER_CMD, getHostPorts());
        CPPUNIT_ASSERT(system(cmd) == 0);
    }

    void stopServer() {
        tearDown();
    }

    void tearDown()
    {
        char cmd[1024];
        sprintf(cmd, "%s stop %s", ZKSERVER_CMD, getHostPorts());
        CPPUNIT_ASSERT(system(cmd) == 0);
    }
    
    void testPing()
    {
        watchctx_t ctxIdle;
        watchctx_t ctxWC;
        zhandle_t *zkIdle = createClient(&ctxIdle);
        zhandle_t *zkWatchCreator = createClient(&ctxWC);
        int rc;
        char path[80];
        CPPUNIT_ASSERT(zkIdle);
        CPPUNIT_ASSERT(zkWatchCreator);
        for(int i = 0; i < 30; i++) {
            sprintf(path, "/%i", i);
            rc = zoo_create(zkWatchCreator, path, "", 0, &ZOO_OPEN_ACL_UNSAFE, 0, 0, 0);
            CPPUNIT_ASSERT_EQUAL((int)ZOK, rc);
        }

        for(int i = 0; i < 30; i++) {
            sprintf(path, "/%i", i);
            struct Stat stat;
            rc = zoo_exists(zkIdle, path, 1, &stat);
            CPPUNIT_ASSERT_EQUAL((int)ZOK, rc);
        }

        for(int i = 0; i < 30; i++) {
            sprintf(path, "/%i", i);
            usleep(500000);
            rc = zoo_delete(zkWatchCreator, path, -1);
            CPPUNIT_ASSERT_EQUAL((int)ZOK, rc);
        }
        struct Stat stat;
        CPPUNIT_ASSERT_EQUAL((int)ZNONODE, zoo_exists(zkIdle, "/0", 0, &stat));
    }

    bool waitForEvent(zhandle_t *zh, watchctx_t *ctx, int seconds) {
        time_t expires = time(0) + seconds;
        while(ctx->countEvents() == 0 && time(0) < expires) {
            yield(zh, 1);
        }
        return ctx->countEvents() > 0;
    }

#define COUNT 100
    
    static zhandle_t *async_zk;
    static volatile int count;
    static char* hp_chroot;
    
    static void statCompletion(int rc, const struct Stat *stat, const void *data) {
        int tmp = (int) (long) data;
        CPPUNIT_ASSERT_EQUAL(tmp, rc);
    }

    static void stringCompletion(int rc, const char *value, const void *data) {
        char *path = (char*)data;
        
        if (rc == ZCONNECTIONLOSS && path) {
            // Try again
            rc = zoo_acreate(async_zk, path, "", 0,  &ZOO_OPEN_ACL_UNSAFE, 0, stringCompletion, 0);
        } else if (rc != ZOK) {
            // fprintf(stderr, "rc = %d with path = %s\n", rc, (path ? path : "null"));
        }
        if (path) {
            free(path);
        }
    }
    
    static void create_completion_fn(int rc, const char* value, const void *data) {
        CPPUNIT_ASSERT_EQUAL((int) ZOK, rc);
        count++;
    }
    
    static void waitForCreateCompletion(int seconds) {
        time_t expires = time(0) + seconds;
        while(count == 0 && time(0) < expires) {
            sleep(1);
        }
        count--;
    }

    static void watcher_chroot_fn(zhandle_t *zh, int type,
                                    int state, const char *path,void *watcherCtx) {
        // check for path
        char *client_path = (char *) watcherCtx;
        CPPUNIT_ASSERT(strcmp(client_path, path) == 0);
        count ++;
    }
    
    static void waitForChrootWatch(int seconds) {
        time_t expires = time(0) + seconds;
        while (count == 0 && time(0) < expires) {
            sleep(1);
        }
        count--;
    }

    static void waitForVoidCompletion(int seconds) {
        time_t expires = time(0) + seconds;
        while(count == 0 && time(0) < expires) {
            sleep(1);
        }
        count--;
    }
    
    static void voidCompletion(int rc, const void *data) {
        int tmp = (int) (long) data;
        CPPUNIT_ASSERT_EQUAL(tmp, rc);
        count++;
    }
    
    static void verifyCreateFails(const char *path, zhandle_t *zk) {
      CPPUNIT_ASSERT_EQUAL((int)ZBADARGUMENTS, zoo_create(zk,
          path, "", 0, &ZOO_OPEN_ACL_UNSAFE, 0, 0, 0));
    }
    
    static void verifyCreateOk(const char *path, zhandle_t *zk) {
      CPPUNIT_ASSERT_EQUAL((int)ZOK, zoo_create(zk,
          path, "", 0, &ZOO_OPEN_ACL_UNSAFE, 0, 0, 0));
    }
    
    static void verifyCreateFailsSeq(const char *path, zhandle_t *zk) {
      CPPUNIT_ASSERT_EQUAL((int)ZBADARGUMENTS, zoo_create(zk,
          path, "", 0, &ZOO_OPEN_ACL_UNSAFE, ZOO_SEQUENCE, 0, 0));
    }
    
    static void verifyCreateOkSeq(const char *path, zhandle_t *zk) {
      CPPUNIT_ASSERT_EQUAL((int)ZOK, zoo_create(zk,
          path, "", 0, &ZOO_OPEN_ACL_UNSAFE, ZOO_SEQUENCE, 0, 0));
    }
    
            
    /**
       returns false if the vectors dont match
    **/
    bool compareAcl(struct ACL_vector acl1, struct ACL_vector acl2) {
        if (acl1.count != acl2.count) {
            return false;
        }
        struct ACL *aclval1 = acl1.data;
        struct ACL *aclval2 = acl2.data;
        if (aclval1->perms != aclval2->perms) {
            return false;
        }
        struct Id id1 = aclval1->id;
        struct Id id2 = aclval2->id;
        if (strcmp(id1.scheme, id2.scheme) != 0) {
            return false;
        }
        if (strcmp(id1.id, id2.id) != 0) {
            return false;
        }
        return true;
    }

    void testAcl() {
        int rc;
        struct ACL_vector aclvec;
        struct Stat stat;
        watchctx_t ctx;
        zhandle_t *zk = createClient(&ctx);
        rc = zoo_create(zk, "/acl", "", 0, 
                        &ZOO_OPEN_ACL_UNSAFE, 0, 0, 0);
        CPPUNIT_ASSERT_EQUAL((int)ZOK, rc);
        rc = zoo_get_acl(zk, "/acl", &aclvec, &stat  );
        CPPUNIT_ASSERT_EQUAL((int) ZOK, rc);
        bool cmp = compareAcl(ZOO_OPEN_ACL_UNSAFE, aclvec);
        CPPUNIT_ASSERT_EQUAL(true, cmp);
        rc = zoo_set_acl(zk, "/acl", -1, &ZOO_READ_ACL_UNSAFE);
        CPPUNIT_ASSERT_EQUAL((int) ZOK, rc);
        rc = zoo_get_acl(zk, "/acl", &aclvec, &stat);
        CPPUNIT_ASSERT_EQUAL((int) ZOK, rc);
        cmp = compareAcl(ZOO_READ_ACL_UNSAFE, aclvec);
        CPPUNIT_ASSERT_EQUAL(true, cmp);
    }


    void testAuth() {
        int rc;
        count = 0;
        watchctx_t ctx1, ctx2, ctx3;
        zhandle_t *zk = createClient(&ctx1);
        struct ACL_vector nodeAcl;
        struct ACL acl_val;
        rc = zoo_add_auth(0, "", 0, 0, voidCompletion, (void*)-1);
        CPPUNIT_ASSERT_EQUAL((int) ZBADARGUMENTS, rc);
        
        rc = zoo_add_auth(zk, 0, 0, 0, voidCompletion, (void*)-1);
        CPPUNIT_ASSERT_EQUAL((int) ZBADARGUMENTS, rc);
        
        // auth as pat, create /tauth1, close session
        rc = zoo_add_auth(zk, "digest", "pat:passwd", 10, voidCompletion,
                          (void*)ZOK);
        CPPUNIT_ASSERT_EQUAL((int) ZOK, rc);
        waitForVoidCompletion(3);
                
        CPPUNIT_ASSERT(count == 0);
        
        rc = zoo_create(zk, "/tauth1", "", 0, &ZOO_CREATOR_ALL_ACL, 0, 0, 0);
        CPPUNIT_ASSERT_EQUAL((int)ZOK, rc);

        zk = createClient(&ctx2);

        rc = zoo_add_auth(zk, "digest", "pat:passwd2", 11, voidCompletion,
                          (void*)ZOK);
        CPPUNIT_ASSERT_EQUAL((int) ZOK, rc);
        waitForVoidCompletion(3);
        CPPUNIT_ASSERT(count == 0);
                
        char buf[1024];
        int blen = sizeof(buf);
        struct Stat stat;
        rc = zoo_get(zk, "/tauth1", 0, buf, &blen, &stat);
        CPPUNIT_ASSERT_EQUAL((int)ZNOAUTH, rc);
        // add auth pat w/correct pass verify success
        rc = zoo_add_auth(zk, "digest", "pat:passwd", 10, voidCompletion,
                          (void*)ZOK);
        
        CPPUNIT_ASSERT_EQUAL((int) ZOK, rc);
        rc = zoo_get(zk, "/tauth1", 0, buf, &blen, &stat);
        CPPUNIT_ASSERT_EQUAL((int)ZOK, rc);
        waitForVoidCompletion(3);
        CPPUNIT_ASSERT(count == 0);
        //create a new client
        zk = createClient(&ctx3);
        rc = zoo_add_auth(zk, "digest", "pat:passwd", 10, voidCompletion, (void*) ZOK);
        waitForVoidCompletion(3);
        CPPUNIT_ASSERT(count == 0);
        rc = zoo_add_auth(zk, "ip", "none", 4, voidCompletion, (void*)ZOK);
        //make the server forget the auths
        waitForVoidCompletion(3);
        CPPUNIT_ASSERT(count == 0);
     
        stopServer();
        CPPUNIT_ASSERT(ctx3.waitForDisconnected(zk));
        startServer();
        CPPUNIT_ASSERT(ctx3.waitForConnected(zk));
        // now try getting the data
        rc = zoo_get(zk, "/tauth1", 0, buf, &blen, &stat);
        CPPUNIT_ASSERT_EQUAL((int)ZOK, rc);
        // also check for get
        rc = zoo_get_acl(zk, "/", &nodeAcl, &stat);
        CPPUNIT_ASSERT_EQUAL((int)ZOK, rc);
        // check if the acl has all the perms
        CPPUNIT_ASSERT_EQUAL((int)1, nodeAcl.count);
        acl_val = *(nodeAcl.data);
        CPPUNIT_ASSERT_EQUAL((int) acl_val.perms, ZOO_PERM_ALL);
        // verify on root node
        rc = zoo_set_acl(zk, "/", -1, &ZOO_CREATOR_ALL_ACL);
        CPPUNIT_ASSERT_EQUAL((int) ZOK, rc);

        rc = zoo_set_acl(zk, "/", -1, &ZOO_OPEN_ACL_UNSAFE);
        CPPUNIT_ASSERT_EQUAL((int) ZOK, rc);
    }
    
    void testNullData() {
        watchctx_t ctx;
        zhandle_t *zk = createClient(&ctx);
        CPPUNIT_ASSERT(zk);
        int rc = 0;
        rc = zoo_create(zk, "/mahadev", NULL, -1, 
                        &ZOO_OPEN_ACL_UNSAFE, 0, 0, 0);
        CPPUNIT_ASSERT_EQUAL((int) ZOK, rc);
        char buffer[512];
        struct Stat stat;
        int len = 512;
        rc = zoo_wget(zk, "/mahadev", NULL, NULL, buffer, &len, &stat);
        CPPUNIT_ASSERT_EQUAL( -1, len);
        CPPUNIT_ASSERT_EQUAL((int) ZOK, rc);
        rc = zoo_set(zk, "/mahadev", NULL, -1, -1);
        CPPUNIT_ASSERT_EQUAL((int) ZOK, rc);
        rc = zoo_wget(zk, "/mahadev", NULL, NULL, buffer, &len, &stat);
        CPPUNIT_ASSERT_EQUAL( -1, len);
        CPPUNIT_ASSERT_EQUAL((int) ZOK, rc);
    }

    void testPathValidation() {
        watchctx_t ctx;
        zhandle_t *zk = createClient(&ctx);
        CPPUNIT_ASSERT(zk);

        verifyCreateFails(0, zk);
        verifyCreateFails("", zk);
        verifyCreateFails("//", zk);
        verifyCreateFails("///", zk);
        verifyCreateFails("////", zk);
        verifyCreateFails("/.", zk);
        verifyCreateFails("/..", zk);
        verifyCreateFails("/./", zk);
        verifyCreateFails("/../", zk);
        verifyCreateFails("/foo/./", zk);
        verifyCreateFails("/foo/../", zk);
        verifyCreateFails("/foo/.", zk);
        verifyCreateFails("/foo/..", zk);
        verifyCreateFails("/./.", zk);
        verifyCreateFails("/../..", zk);
        verifyCreateFails("/foo/bar/", zk);
        verifyCreateFails("/foo//bar", zk);
        verifyCreateFails("/foo/bar//", zk);

        verifyCreateFails("foo", zk);
        verifyCreateFails("a", zk);

        // verify that trailing fails, except for seq which adds suffix
        verifyCreateOk("/createseq", zk);
        verifyCreateFails("/createseq/", zk);
        verifyCreateOkSeq("/createseq/", zk);
        verifyCreateOkSeq("/createseq/.", zk);
        verifyCreateOkSeq("/createseq/..", zk);
        verifyCreateFailsSeq("/createseq//", zk);
        verifyCreateFailsSeq("/createseq/./", zk);
        verifyCreateFailsSeq("/createseq/../", zk);

        verifyCreateOk("/.foo", zk);
        verifyCreateOk("/.f.", zk);
        verifyCreateOk("/..f", zk);
        verifyCreateOk("/..f..", zk);
        verifyCreateOk("/f.c", zk);
        verifyCreateOk("/f", zk);
        verifyCreateOk("/f/.f", zk);
        verifyCreateOk("/f/f.", zk);
        verifyCreateOk("/f/..f", zk);
        verifyCreateOk("/f/f..", zk);
        verifyCreateOk("/f/.f/f", zk);
        verifyCreateOk("/f/f./f", zk);
    }
    
    void testChroot() {
        // the c client async callbacks do 
        // not callback with the path, so 
        // we dont need to test taht for now
        // we should fix that though soon!
        watchctx_t ctx, ctx_ch;
        zhandle_t *zk, *zk_ch;
        char buf[60];
        int rc, len;
        struct Stat stat;
        const char* data = "garbage";
        const char* retStr = "/chroot"; 
        const char* root= "/";
        hp_chroot = "127.0.0.1:22181/test/mahadev";
        zk_ch = createchClient(&ctx_ch);
        CPPUNIT_ASSERT(zk_ch != NULL);
        zk = createClient(&ctx);
        rc = zoo_create(zk, "/test", "", 0, &ZOO_OPEN_ACL_UNSAFE, 0, 0, 0);
        CPPUNIT_ASSERT_EQUAL((int) ZOK, rc);
        rc = zoo_create(zk, "/test/mahadev", data, 7, &ZOO_OPEN_ACL_UNSAFE, 0, 0, 0);
        CPPUNIT_ASSERT_EQUAL((int) ZOK, rc);
        // try an exists with /
        len = 60;
        rc = zoo_get(zk_ch, "/", 0, buf, &len, &stat);
        CPPUNIT_ASSERT_EQUAL((int)ZOK, rc);
        //check if the data is the same
        CPPUNIT_ASSERT(strncmp(buf, data, 7) == 0);
        //check for watches 
        rc = zoo_wexists(zk_ch, "/chroot", watcher_chroot_fn, (void *) retStr, &stat);
        //now check if we can do create/delete/get/sets/acls/getChildren and others 
        //check create
        rc = zoo_create(zk_ch, "/chroot", "", 0, &ZOO_OPEN_ACL_UNSAFE, 0, 0,0);
        CPPUNIT_ASSERT_EQUAL((int) ZOK, rc);
        waitForChrootWatch(3);
        CPPUNIT_ASSERT(count == 0);
        rc = zoo_create(zk_ch, "/chroot/child", "", 0, &ZOO_OPEN_ACL_UNSAFE, 0, 0, 0);
        CPPUNIT_ASSERT_EQUAL((int) ZOK, rc);
        rc = zoo_exists(zk, "/test/mahadev/chroot/child", 0, &stat);
        CPPUNIT_ASSERT_EQUAL((int) ZOK, rc);
        
        rc = zoo_delete(zk_ch, "/chroot/child", -1);
        CPPUNIT_ASSERT_EQUAL((int) ZOK, rc);
        rc = zoo_exists(zk, "/test/mahadev/chroot/child", 0, &stat);
        CPPUNIT_ASSERT_EQUAL((int) ZNONODE, rc);
        rc = zoo_wget(zk_ch, "/chroot", watcher_chroot_fn, (char*) retStr,
                      buf, &len, &stat);
        CPPUNIT_ASSERT_EQUAL((int) ZOK, rc);
        rc = zoo_set(zk_ch, "/chroot",buf, 3, -1);
        CPPUNIT_ASSERT_EQUAL((int) ZOK, rc);
        waitForChrootWatch(3);
        CPPUNIT_ASSERT(count == 0);
        // check for getchildren
        struct String_vector children;
        rc = zoo_get_children(zk_ch, "/", 0, &children);
        CPPUNIT_ASSERT_EQUAL((int)ZOK, rc);
        CPPUNIT_ASSERT_EQUAL((int)1, children.count);
        //check if te child if chroot
        CPPUNIT_ASSERT(strcmp((retStr+1), children.data[0]) == 0);
        // check for get/set acl
        struct ACL_vector acl;
        rc = zoo_get_acl(zk_ch, "/", &acl, &stat);
        CPPUNIT_ASSERT_EQUAL((int)ZOK, rc);
        CPPUNIT_ASSERT_EQUAL((int)1, acl.count);
        CPPUNIT_ASSERT_EQUAL(ZOO_PERM_ALL, acl.data->perms);
        // set acl
        rc = zoo_set_acl(zk_ch, "/chroot", -1,  &ZOO_READ_ACL_UNSAFE);
        CPPUNIT_ASSERT_EQUAL((int) ZOK, rc);
        // see if you add children
        rc = zoo_create(zk_ch, "/chroot/child1", "",0, &ZOO_OPEN_ACL_UNSAFE, 0, 0, 0);
        CPPUNIT_ASSERT_EQUAL((int)ZNOAUTH, rc);
        //add wget children test
        rc = zoo_wget_children(zk_ch, "/", watcher_chroot_fn, (char*) root, &children);
        CPPUNIT_ASSERT_EQUAL((int)ZOK, rc);
        
        //now create a node
        rc = zoo_create(zk_ch, "/child2", "",0, &ZOO_OPEN_ACL_UNSAFE, 0, 0, 0);
        CPPUNIT_ASSERT_EQUAL((int) ZOK, rc);
        waitForChrootWatch(3);
        CPPUNIT_ASSERT(count == 0);
        //check for one async call just to make sure
        rc = zoo_acreate(zk_ch, "/child3", "", 0, &ZOO_OPEN_ACL_UNSAFE, 0, 
                         create_completion_fn, 0);
        waitForCreateCompletion(3);
        CPPUNIT_ASSERT(count == 0);
    }
        
    void testAsyncWatcherAutoReset()
    {
        watchctx_t ctx;
        zhandle_t *zk = createClient(&ctx);
        watchctx_t lctx[COUNT];
        int i;
        char path[80];
        int rc;
        evt_t evt;

        async_zk = zk;
        for(i = 0; i < COUNT; i++) {
            sprintf(path, "/%d", i);
            rc = zoo_awexists(zk, path, watcher, &lctx[i], statCompletion, (void*)ZNONODE);
            CPPUNIT_ASSERT_EQUAL((int)ZOK, rc);
        }

        yield(zk, 0);

        for(i = 0; i < COUNT/2; i++) {
            sprintf(path, "/%d", i);
            rc = zoo_acreate(zk, path, "", 0,  &ZOO_OPEN_ACL_UNSAFE, 0, stringCompletion, strdup(path));
            CPPUNIT_ASSERT_EQUAL((int)ZOK, rc);
        }

        yield(zk, 3);
        for(i = 0; i < COUNT/2; i++) {
            sprintf(path, "/%d", i);
            CPPUNIT_ASSERT_MESSAGE(path, waitForEvent(zk, &lctx[i], 5));
            evt = lctx[i].getEvent();
            CPPUNIT_ASSERT_EQUAL_MESSAGE(evt.path.c_str(), ZOO_CREATED_EVENT, evt.type);
            CPPUNIT_ASSERT_EQUAL(string(path), evt.path);
        }

        for(i = COUNT/2 + 1; i < COUNT*10; i++) {
            sprintf(path, "/%d", i);
            rc = zoo_acreate(zk, path, "", 0,  &ZOO_OPEN_ACL_UNSAFE, 0, stringCompletion, strdup(path));
            CPPUNIT_ASSERT_EQUAL((int)ZOK, rc);
        }
        
        yield(zk, 1);
        stopServer();
        CPPUNIT_ASSERT(ctx.waitForDisconnected(zk));
        startServer();
        CPPUNIT_ASSERT(ctx.waitForConnected(zk));
        yield(zk, 3);
        for(i = COUNT/2+1; i < COUNT; i++) {
            sprintf(path, "/%d", i);
            CPPUNIT_ASSERT_MESSAGE(path, waitForEvent(zk, &lctx[i], 5));
            evt = lctx[i].getEvent();
            CPPUNIT_ASSERT_EQUAL_MESSAGE(evt.path, ZOO_CREATED_EVENT, evt.type);
            CPPUNIT_ASSERT_EQUAL(string(path), evt.path);
        }
    }

    void testWatcherAutoReset(zhandle_t *zk, watchctx_t *ctxGlobal, 
                              watchctx_t *ctxLocal)
    {
        bool isGlobal = (ctxGlobal == ctxLocal);
        int rc;
        struct Stat stat;
        char buf[1024];
        int blen;
        struct String_vector strings;
        const char *testName;

        rc = zoo_create(zk, "/watchtest", "", 0, 
                        &ZOO_OPEN_ACL_UNSAFE, 0, 0, 0);
        CPPUNIT_ASSERT_EQUAL((int)ZOK, rc);
        rc = zoo_create(zk, "/watchtest/child", "", 0,
                        &ZOO_OPEN_ACL_UNSAFE, ZOO_EPHEMERAL, 0, 0);
        CPPUNIT_ASSERT_EQUAL((int)ZOK, rc);

        if (isGlobal) {
            testName = "GlobalTest";
            rc = zoo_get_children(zk, "/watchtest", 1, &strings);
            deallocate_String_vector(&strings);
            CPPUNIT_ASSERT_EQUAL((int)ZOK, rc);
            blen = sizeof(buf);
            rc = zoo_get(zk, "/watchtest/child", 1, buf, &blen, &stat);
            CPPUNIT_ASSERT_EQUAL((int)ZOK, rc);
            rc = zoo_exists(zk, "/watchtest/child2", 1, &stat);
            CPPUNIT_ASSERT_EQUAL((int)ZNONODE, rc);
        } else {
            testName = "LocalTest";
            rc = zoo_wget_children(zk, "/watchtest", watcher, ctxLocal,
                                 &strings);
            deallocate_String_vector(&strings);
            CPPUNIT_ASSERT_EQUAL((int)ZOK, rc);
            blen = sizeof(buf);
            rc = zoo_wget(zk, "/watchtest/child", watcher, ctxLocal,
                         buf, &blen, &stat);
            CPPUNIT_ASSERT_EQUAL((int)ZOK, rc);
            rc = zoo_wexists(zk, "/watchtest/child2", watcher, ctxLocal,
                            &stat);
            CPPUNIT_ASSERT_EQUAL((int)ZNONODE, rc);
        }
        
        CPPUNIT_ASSERT(ctxLocal->countEvents() == 0);

        stopServer();
        CPPUNIT_ASSERT_MESSAGE(testName, ctxGlobal->waitForDisconnected(zk));
        startServer();
        CPPUNIT_ASSERT_MESSAGE(testName, ctxLocal->waitForConnected(zk));

        CPPUNIT_ASSERT(ctxLocal->countEvents() == 0);

        rc = zoo_set(zk, "/watchtest/child", "1", 1, -1);
        CPPUNIT_ASSERT_EQUAL((int)ZOK, rc);
        struct Stat stat1, stat2;
        rc = zoo_set2(zk, "/watchtest/child", "1", 1, -1, &stat1);
        CPPUNIT_ASSERT_EQUAL((int)ZOK, rc);
        CPPUNIT_ASSERT(stat1.version >= 0);
        rc = zoo_set2(zk, "/watchtest/child", "1", 1, stat1.version, &stat2);
        CPPUNIT_ASSERT_EQUAL((int)ZOK, rc);
        rc = zoo_set(zk, "/watchtest/child", "1", 1, stat2.version);
        CPPUNIT_ASSERT_EQUAL((int)ZOK, rc);
        rc = zoo_create(zk, "/watchtest/child2", "", 0,
                        &ZOO_OPEN_ACL_UNSAFE, 0, 0, 0);
        CPPUNIT_ASSERT_EQUAL((int)ZOK, rc);

        CPPUNIT_ASSERT_MESSAGE(testName, waitForEvent(zk, ctxLocal, 5));
        
        evt_t evt = ctxLocal->getEvent();
        CPPUNIT_ASSERT_EQUAL_MESSAGE(evt.path, ZOO_CHANGED_EVENT, evt.type);
        CPPUNIT_ASSERT_EQUAL(string("/watchtest/child"), evt.path);

        CPPUNIT_ASSERT_MESSAGE(testName, waitForEvent(zk, ctxLocal, 5));
        // The create will trigget the get children and the
        // exists watches
        evt = ctxLocal->getEvent();
        CPPUNIT_ASSERT_EQUAL_MESSAGE(evt.path, ZOO_CREATED_EVENT, evt.type);
        CPPUNIT_ASSERT_EQUAL(string("/watchtest/child2"), evt.path);
        CPPUNIT_ASSERT_MESSAGE(testName, waitForEvent(zk, ctxLocal, 5));
        evt = ctxLocal->getEvent();
        CPPUNIT_ASSERT_EQUAL_MESSAGE(evt.path, ZOO_CHILD_EVENT, evt.type);
        CPPUNIT_ASSERT_EQUAL(string("/watchtest"), evt.path);

        // Make sure Pings are giving us problems
        sleep(5);

        CPPUNIT_ASSERT(ctxLocal->countEvents() == 0);
        
        stopServer();
        CPPUNIT_ASSERT_MESSAGE(testName, ctxGlobal->waitForDisconnected(zk));
        startServer();
        CPPUNIT_ASSERT_MESSAGE(testName, ctxGlobal->waitForConnected(zk));

        if (isGlobal) {
            testName = "GlobalTest";
            rc = zoo_get_children(zk, "/watchtest", 1, &strings);
            deallocate_String_vector(&strings);
            CPPUNIT_ASSERT_EQUAL((int)ZOK, rc);
            blen = sizeof(buf);
            rc = zoo_get(zk, "/watchtest/child", 1, buf, &blen, &stat);
            CPPUNIT_ASSERT_EQUAL((int)ZOK, rc);
            rc = zoo_exists(zk, "/watchtest/child2", 1, &stat);
            CPPUNIT_ASSERT_EQUAL((int)ZOK, rc);
        } else {
            testName = "LocalTest";
            rc = zoo_wget_children(zk, "/watchtest", watcher, ctxLocal,
                                 &strings);
            deallocate_String_vector(&strings);
            CPPUNIT_ASSERT_EQUAL((int)ZOK, rc);
            blen = sizeof(buf);
            rc = zoo_wget(zk, "/watchtest/child", watcher, ctxLocal,
                         buf, &blen, &stat);
            CPPUNIT_ASSERT_EQUAL((int)ZOK, rc);
            rc = zoo_wexists(zk, "/watchtest/child2", watcher, ctxLocal,
                            &stat);
            CPPUNIT_ASSERT_EQUAL((int)ZOK, rc);
        }

        zoo_delete(zk, "/watchtest/child2", -1);

        CPPUNIT_ASSERT_MESSAGE(testName, waitForEvent(zk, ctxLocal, 5));
        
        evt = ctxLocal->getEvent();
        CPPUNIT_ASSERT_EQUAL_MESSAGE(evt.path, ZOO_DELETED_EVENT, evt.type);
        CPPUNIT_ASSERT_EQUAL(string("/watchtest/child2"), evt.path);
        
        CPPUNIT_ASSERT_MESSAGE(testName, waitForEvent(zk, ctxLocal, 5));
        evt = ctxLocal->getEvent();
        CPPUNIT_ASSERT_EQUAL_MESSAGE(evt.path, ZOO_CHILD_EVENT, evt.type);
        CPPUNIT_ASSERT_EQUAL(string("/watchtest"), evt.path);

        stopServer();
        CPPUNIT_ASSERT_MESSAGE(testName, ctxGlobal->waitForDisconnected(zk));
        startServer();
        CPPUNIT_ASSERT_MESSAGE(testName, ctxLocal->waitForConnected(zk));

        zoo_delete(zk, "/watchtest/child", -1);
        zoo_delete(zk, "/watchtest", -1);

        CPPUNIT_ASSERT_MESSAGE(testName, waitForEvent(zk, ctxLocal, 5));
        
        evt = ctxLocal->getEvent();
        CPPUNIT_ASSERT_EQUAL_MESSAGE(evt.path, ZOO_DELETED_EVENT, evt.type);
        CPPUNIT_ASSERT_EQUAL(string("/watchtest/child"), evt.path);

        // Make sure nothing is straggling
        sleep(1);
        CPPUNIT_ASSERT(ctxLocal->countEvents() == 0);
    }        

    void testWatcherAutoResetWithGlobal()
    {
        watchctx_t ctx;
        zhandle_t *zk = createClient(&ctx);
        testWatcherAutoReset(zk, &ctx, &ctx);
    }

    void testWatcherAutoResetWithLocal()
    {
        watchctx_t ctx;
        watchctx_t lctx;
        zhandle_t *zk = createClient(&ctx);
        testWatcherAutoReset(zk, &ctx, &lctx);
    }
};

volatile int Zookeeper_simpleSystem::count;
zhandle_t *Zookeeper_simpleSystem::async_zk;
const char Zookeeper_simpleSystem::hostPorts[] = "127.0.0.1:22181";
char* Zookeeper_simpleSystem::hp_chroot;
CPPUNIT_TEST_SUITE_REGISTRATION(Zookeeper_simpleSystem);
