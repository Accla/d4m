// $ANTLR 3.0.1 /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g 2009-01-07 13:41:40

package org.apache.hadoop.hive.ql.parse;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import org.antlr.runtime.tree.*;

public class HiveParser extends Parser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "TOK_INSERT", "TOK_QUERY", "TOK_SELECT", "TOK_SELECTDI", "TOK_SELEXPR", "TOK_FROM", "TOK_TAB", "TOK_PARTSPEC", "TOK_PARTVAL", "TOK_DIR", "TOK_LOCAL_DIR", "TOK_TABREF", "TOK_SUBQUERY", "TOK_DESTINATION", "TOK_ALLCOLREF", "TOK_COLREF", "TOK_FUNCTION", "TOK_FUNCTIONDI", "TOK_WHERE", "TOK_OP_EQ", "TOK_OP_NE", "TOK_OP_LE", "TOK_OP_LT", "TOK_OP_GE", "TOK_OP_GT", "TOK_OP_DIV", "TOK_OP_ADD", "TOK_OP_SUB", "TOK_OP_MUL", "TOK_OP_MOD", "TOK_OP_BITAND", "TOK_OP_BITNOT", "TOK_OP_BITOR", "TOK_OP_BITXOR", "TOK_OP_AND", "TOK_OP_OR", "TOK_OP_NOT", "TOK_OP_LIKE", "TOK_TRUE", "TOK_FALSE", "TOK_TRANSFORM", "TOK_COLLIST", "TOK_ALIASLIST", "TOK_GROUPBY", "TOK_ORDERBY", "TOK_CLUSTERBY", "TOK_UNION", "TOK_JOIN", "TOK_LEFTOUTERJOIN", "TOK_RIGHTOUTERJOIN", "TOK_FULLOUTERJOIN", "TOK_LOAD", "TOK_NULL", "TOK_ISNULL", "TOK_ISNOTNULL", "TOK_TINYINT", "TOK_INT", "TOK_BIGINT", "TOK_BOOLEAN", "TOK_FLOAT", "TOK_DOUBLE", "TOK_DATE", "TOK_DATETIME", "TOK_TIMESTAMP", "TOK_STRING", "TOK_LIST", "TOK_MAP", "TOK_CREATETABLE", "TOK_DESCTABLE", "TOK_ALTERTABLE_RENAME", "TOK_ALTERTABLE_ADDCOLS", "TOK_ALTERTABLE_REPLACECOLS", "TOK_ALTERTABLE_DROPPARTS", "TOK_SHOWTABLES", "TOK_SHOWPARTITIONS", "TOK_CREATEEXTTABLE", "TOK_DROPTABLE", "TOK_TABCOLLIST", "TOK_TABCOL", "TOK_TABLECOMMENT", "TOK_TABLEPARTCOLS", "TOK_TABLEBUCKETS", "TOK_TABLEROWFORMAT", "TOK_TABLEROWFORMATFIELD", "TOK_TABLEROWFORMATCOLLITEMS", "TOK_TABLEROWFORMATMAPKEYS", "TOK_TABLEROWFORMATLINES", "TOK_TBLSEQUENCEFILE", "TOK_TABCOLNAME", "TOK_TABLELOCATION", "TOK_TABLESAMPLE", "TOK_TMP_FILE", "TOK_TABSORTCOLNAMEASC", "TOK_TABSORTCOLNAMEDESC", "TOK_CHARSETLITERAL", "TOK_CREATEFUNCTION", "TOK_EXPLAIN", "TOK_TABLESERIALIZER", "TOK_TABLSERDEPROPERTIES", "TOK_TABLESERDEPROPLIST", "TOK_LIMIT", "TOKTABLESERDEPROPERTY", "KW_EXPLAIN", "KW_EXTENDED", "KW_LOAD", "KW_DATA", "KW_LOCAL", "KW_INPATH", "StringLiteral", "KW_OVERWRITE", "KW_INTO", "KW_TABLE", "KW_CREATE", "KW_EXTERNAL", "Identifier", "LPAREN", "RPAREN", "KW_DROP", "KW_ALTER", "KW_RENAME", "KW_TO", "KW_ADD", "KW_REPLACE", "KW_COLUMNS", "COMMA", "KW_DESCRIBE", "KW_SHOW", "KW_TABLES", "KW_PARTITIONS", "KW_TEMPORARY", "KW_FUNCTION", "KW_AS", "KW_COMMENT", "KW_PARTITIONED", "KW_BY", "KW_CLUSTERED", "KW_SORTED", "Number", "KW_BUCKETS", "KW_ROW", "KW_FORMAT", "KW_DELIMITED", "KW_SERIALIZER", "KW_WITH", "KW_PROPERTIES", "EQUAL", "KW_FIELDS", "KW_TERMINATED", "KW_COLLECTION", "KW_ITEMS", "KW_MAP", "KW_KEYS", "KW_LINES", "KW_STORED", "KW_SEQUENCEFILE", "KW_LOCATION", "KW_ASC", "KW_DESC", "KW_TINYINT", "KW_INT", "KW_BIGINT", "KW_BOOLEAN", "KW_FLOAT", "KW_DOUBLE", "KW_DATE", "KW_DATETIME", "KW_TIMESTAMP", "KW_STRING", "KW_ARRAY", "LESSTHAN", "GREATERTHAN", "KW_UNION", "KW_ALL", "KW_INSERT", "KW_DIRECTORY", "KW_LIMIT", "KW_SELECT", "KW_DISTINCT", "KW_TRANSFORM", "KW_USING", "DOT", "STAR", "KW_FROM", "KW_ON", "KW_JOIN", "KW_LEFT", "KW_OUTER", "KW_RIGHT", "KW_FULL", "KW_TABLESAMPLE", "KW_BUCKET", "KW_OUT", "KW_OF", "KW_WHERE", "KW_GROUP", "KW_ORDER", "KW_CLUSTER", "KW_CAST", "CharSetName", "CharSetLiteral", "KW_NULL", "LSQUARE", "RSQUARE", "PLUS", "MINUS", "TILDE", "KW_IS", "KW_NOT", "BITWISEXOR", "DIVIDE", "MOD", "AMPERSAND", "BITWISEOR", "NOTEQUAL", "LESSTHANOREQUALTO", "GREATERTHANOREQUALTO", "KW_LIKE", "KW_RLIKE", "KW_REGEXP", "KW_AND", "KW_OR", "KW_TRUE", "KW_FALSE", "KW_PARTITION", "COLON", "SEMICOLON", "Letter", "HexDigit", "Digit", "Exponent", "WS", "COMMENT"
    };
    public static final int KW_ALTER=122;
    public static final int TOK_FUNCTIONDI=21;
    public static final int KW_LIKE=220;
    public static final int TOK_TABREF=15;
    public static final int KW_PARTITIONED=137;
    public static final int KW_JOIN=188;
    public static final int STAR=185;
    public static final int LSQUARE=205;
    public static final int MOD=214;
    public static final int KW_ITEMS=153;
    public static final int TOK_OP_EQ=23;
    public static final int KW_ROW=143;
    public static final int KW_FORMAT=144;
    public static final int TOK_OP_GT=28;
    public static final int KW_FLOAT=166;
    public static final int KW_DROP=121;
    public static final int EOF=-1;
    public static final int KW_EXPLAIN=106;
    public static final int TOK_OP_GE=27;
    public static final int KW_ASC=160;
    public static final int RPAREN=120;
    public static final int TOK_OP_ADD=30;
    public static final int TOK_TABCOLNAME=92;
    public static final int KW_PARTITIONS=132;
    public static final int TOK_TABLESAMPLE=94;
    public static final int TOK_FROM=9;
    public static final int TOK_QUERY=5;
    public static final int TOK_RIGHTOUTERJOIN=53;
    public static final int TOK_TINYINT=59;
    public static final int KW_TABLE=115;
    public static final int DIVIDE=213;
    public static final int TOK_TABLSERDEPROPERTIES=102;
    public static final int TOK_BIGINT=61;
    public static final int KW_ADD=125;
    public static final int TOK_ALTERTABLE_RENAME=73;
    public static final int SEMICOLON=229;
    public static final int TOK_TABCOL=82;
    public static final int KW_TRANSFORM=182;
    public static final int TOK_WHERE=22;
    public static final int KW_SERIALIZER=146;
    public static final int TOK_TABLESERDEPROPLIST=103;
    public static final int TOK_TBLSEQUENCEFILE=91;
    public static final int WS=234;
    public static final int TOK_SELEXPR=8;
    public static final int KW_SELECT=180;
    public static final int TOK_TABLEROWFORMATFIELD=87;
    public static final int TOK_MAP=70;
    public static final int KW_REPLACE=126;
    public static final int KW_BUCKET=194;
    public static final int KW_GROUP=198;
    public static final int KW_LOAD=108;
    public static final int KW_TO=124;
    public static final int KW_BY=138;
    public static final int TOK_UNION=50;
    public static final int TOK_SELECT=6;
    public static final int TOK_OP_LIKE=41;
    public static final int KW_LOCAL=110;
    public static final int KW_NOT=211;
    public static final int TOK_TMP_FILE=95;
    public static final int KW_INPATH=111;
    public static final int KW_OUT=195;
    public static final int KW_LINES=156;
    public static final int KW_AND=223;
    public static final int TOK_SUBQUERY=16;
    public static final int CharSetName=202;
    public static final int KW_BOOLEAN=165;
    public static final int TOK_DOUBLE=64;
    public static final int KW_REGEXP=222;
    public static final int TOK_DESCTABLE=72;
    public static final int TOK_DATETIME=66;
    public static final int KW_DIRECTORY=178;
    public static final int HexDigit=231;
    public static final int KW_PARTITION=227;
    public static final int LPAREN=119;
    public static final int KW_FROM=186;
    public static final int GREATERTHANOREQUALTO=219;
    public static final int TOK_TIMESTAMP=67;
    public static final int TOK_TRUE=42;
    public static final int KW_TERMINATED=151;
    public static final int TOK_CREATEEXTTABLE=79;
    public static final int TOK_LOCAL_DIR=14;
    public static final int PLUS=207;
    public static final int KW_IS=210;
    public static final int KW_OUTER=190;
    public static final int TOK_TABSORTCOLNAMEDESC=97;
    public static final int KW_EXTENDED=107;
    public static final int TOK_LOAD=55;
    public static final int TOK_COLREF=19;
    public static final int KW_LOCATION=159;
    public static final int KW_ORDER=199;
    public static final int TOK_TRANSFORM=44;
    public static final int LESSTHAN=173;
    public static final int KW_ALL=176;
    public static final int KW_DELIMITED=145;
    public static final int TOK_ISNULL=57;
    public static final int TOK_ALLCOLREF=18;
    public static final int TOK_FUNCTION=20;
    public static final int TOK_TABLEROWFORMATLINES=90;
    public static final int TOK_DIR=13;
    public static final int TOK_CREATEFUNCTION=99;
    public static final int AMPERSAND=215;
    public static final int TOK_SHOWTABLES=77;
    public static final int MINUS=208;
    public static final int KW_FIELDS=150;
    public static final int KW_SEQUENCEFILE=158;
    public static final int BITWISEOR=216;
    public static final int TOK_FALSE=43;
    public static final int COLON=228;
    public static final int StringLiteral=112;
    public static final int TOK_TABLECOMMENT=83;
    public static final int CharSetLiteral=203;
    public static final int TOK_OP_LT=26;
    public static final int KW_CLUSTERED=139;
    public static final int KW_TABLESAMPLE=193;
    public static final int TOK_FULLOUTERJOIN=54;
    public static final int KW_COMMENT=136;
    public static final int TOK_OP_LE=25;
    public static final int KW_USING=183;
    public static final int KW_NULL=204;
    public static final int TOK_OP_AND=38;
    public static final int TOK_OP_MOD=33;
    public static final int TOK_OP_BITXOR=37;
    public static final int KW_TINYINT=162;
    public static final int TOK_ALTERTABLE_ADDCOLS=74;
    public static final int KW_DATETIME=169;
    public static final int KW_STRING=171;
    public static final int TOK_GROUPBY=47;
    public static final int TOK_CHARSETLITERAL=98;
    public static final int TOK_TABLEPARTCOLS=84;
    public static final int KW_COLLECTION=152;
    public static final int TOK_ALTERTABLE_DROPPARTS=76;
    public static final int KW_INSERT=177;
    public static final int BITWISEXOR=212;
    public static final int TOK_DROPTABLE=80;
    public static final int TOK_NULL=56;
    public static final int TOK_OP_OR=39;
    public static final int KW_WHERE=197;
    public static final int TOK_TABLEROWFORMATMAPKEYS=89;
    public static final int Identifier=118;
    public static final int TOK_OP_DIV=29;
    public static final int NOTEQUAL=217;
    public static final int TOK_PARTVAL=12;
    public static final int TOK_OP_NE=24;
    public static final int TOK_TABLEBUCKETS=85;
    public static final int KW_RLIKE=221;
    public static final int TOK_DATE=65;
    public static final int TOK_OP_NOT=40;
    public static final int COMMENT=235;
    public static final int TOK_TABLEROWFORMAT=86;
    public static final int KW_OVERWRITE=113;
    public static final int KW_DISTINCT=181;
    public static final int GREATERTHAN=174;
    public static final int TOK_SHOWPARTITIONS=78;
    public static final int TOK_ISNOTNULL=58;
    public static final int KW_CLUSTER=200;
    public static final int KW_FUNCTION=134;
    public static final int TOK_ALIASLIST=46;
    public static final int TOK_LIST=69;
    public static final int TOK_INSERT=4;
    public static final int KW_INT=163;
    public static final int KW_RENAME=123;
    public static final int KW_LEFT=189;
    public static final int KW_KEYS=155;
    public static final int KW_DOUBLE=167;
    public static final int TOK_TABLESERIALIZER=101;
    public static final int TOK_ALTERTABLE_REPLACECOLS=75;
    public static final int TOK_LEFTOUTERJOIN=52;
    public static final int KW_SORTED=140;
    public static final int KW_MAP=154;
    public static final int TOK_COLLIST=45;
    public static final int TOK_STRING=68;
    public static final int KW_FULL=192;
    public static final int TOK_CLUSTERBY=49;
    public static final int TOK_FLOAT=63;
    public static final int LESSTHANOREQUALTO=218;
    public static final int KW_TABLES=131;
    public static final int KW_ARRAY=172;
    public static final int KW_BUCKETS=142;
    public static final int Letter=230;
    public static final int KW_TIMESTAMP=170;
    public static final int TOK_SELECTDI=7;
    public static final int KW_COLUMNS=127;
    public static final int TOK_OP_MUL=32;
    public static final int KW_DESCRIBE=129;
    public static final int TOK_CREATETABLE=71;
    public static final int KW_DESC=161;
    public static final int Exponent=233;
    public static final int KW_CREATE=116;
    public static final int KW_TRUE=225;
    public static final int KW_LIMIT=179;
    public static final int KW_WITH=147;
    public static final int KW_BIGINT=164;
    public static final int TOK_INT=60;
    public static final int TOKTABLESERDEPROPERTY=105;
    public static final int KW_RIGHT=191;
    public static final int TOK_ORDERBY=48;
    public static final int KW_EXTERNAL=117;
    public static final int Number=141;
    public static final int COMMA=128;
    public static final int EQUAL=149;
    public static final int TILDE=209;
    public static final int TOK_JOIN=51;
    public static final int TOK_DESTINATION=17;
    public static final int TOK_OP_BITAND=34;
    public static final int DOT=184;
    public static final int TOK_TAB=10;
    public static final int KW_UNION=175;
    public static final int KW_TEMPORARY=133;
    public static final int KW_CAST=201;
    public static final int KW_FALSE=226;
    public static final int TOK_EXPLAIN=100;
    public static final int TOK_OP_BITOR=36;
    public static final int RSQUARE=206;
    public static final int Digit=232;
    public static final int TOK_PARTSPEC=11;
    public static final int KW_STORED=157;
    public static final int TOK_BOOLEAN=62;
    public static final int KW_DATA=109;
    public static final int TOK_LIMIT=104;
    public static final int TOK_TABSORTCOLNAMEASC=96;
    public static final int TOK_OP_BITNOT=35;
    public static final int KW_SHOW=130;
    public static final int TOK_TABCOLLIST=81;
    public static final int KW_DATE=168;
    public static final int KW_INTO=114;
    public static final int KW_OR=224;
    public static final int TOK_TABLEROWFORMATCOLLITEMS=88;
    public static final int KW_ON=187;
    public static final int KW_AS=135;
    public static final int KW_OF=196;
    public static final int TOK_OP_SUB=31;
    public static final int TOK_TABLELOCATION=93;
    public static final int KW_PROPERTIES=148;

        public HiveParser(TokenStream input) {
            super(input);
            ruleMemo = new HashMap[245+1];
         }
        
    protected TreeAdaptor adaptor = new CommonTreeAdaptor();

    public void setTreeAdaptor(TreeAdaptor adaptor) {
        this.adaptor = adaptor;
    }
    public TreeAdaptor getTreeAdaptor() {
        return adaptor;
    }

    public String[] getTokenNames() { return tokenNames; }
    public String getGrammarFileName() { return "/root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g"; }


    public static class statement_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start statement
    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:130:1: statement : ( explainStatement EOF | execStatement EOF );
    public final statement_return statement() throws RecognitionException {
        statement_return retval = new statement_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token EOF2=null;
        Token EOF4=null;
        explainStatement_return explainStatement1 = null;

        execStatement_return execStatement3 = null;


        CommonTree EOF2_tree=null;
        CommonTree EOF4_tree=null;

        try {
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:131:2: ( explainStatement EOF | execStatement EOF )
            int alt1=2;
            int LA1_0 = input.LA(1);

            if ( (LA1_0==KW_EXPLAIN) ) {
                alt1=1;
            }
            else if ( (LA1_0==KW_LOAD||LA1_0==KW_CREATE||(LA1_0>=KW_DROP && LA1_0<=KW_ALTER)||(LA1_0>=KW_DESCRIBE && LA1_0<=KW_SHOW)||LA1_0==KW_INSERT||LA1_0==KW_SELECT||LA1_0==KW_FROM) ) {
                alt1=2;
            }
            else {
                if (backtracking>0) {failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("130:1: statement : ( explainStatement EOF | execStatement EOF );", 1, 0, input);

                throw nvae;
            }
            switch (alt1) {
                case 1 :
                    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:131:4: explainStatement EOF
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_explainStatement_in_statement367);
                    explainStatement1=explainStatement();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, explainStatement1.getTree());
                    EOF2=(Token)input.LT(1);
                    match(input,EOF,FOLLOW_EOF_in_statement369); if (failed) return retval;
                    if ( backtracking==0 ) {
                    EOF2_tree = (CommonTree)adaptor.create(EOF2);
                    adaptor.addChild(root_0, EOF2_tree);
                    }

                    }
                    break;
                case 2 :
                    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:132:4: execStatement EOF
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_execStatement_in_statement374);
                    execStatement3=execStatement();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, execStatement3.getTree());
                    EOF4=(Token)input.LT(1);
                    match(input,EOF,FOLLOW_EOF_in_statement376); if (failed) return retval;
                    if ( backtracking==0 ) {
                    EOF4_tree = (CommonTree)adaptor.create(EOF4);
                    adaptor.addChild(root_0, EOF4_tree);
                    }

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e) {
          reportError(e);
          throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end statement

    public static class explainStatement_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start explainStatement
    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:135:1: explainStatement : KW_EXPLAIN (isExtended= KW_EXTENDED )? execStatement -> ^( TOK_EXPLAIN execStatement ( $isExtended)? ) ;
    public final explainStatement_return explainStatement() throws RecognitionException {
        explainStatement_return retval = new explainStatement_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token isExtended=null;
        Token KW_EXPLAIN5=null;
        execStatement_return execStatement6 = null;


        CommonTree isExtended_tree=null;
        CommonTree KW_EXPLAIN5_tree=null;
        RewriteRuleTokenStream stream_KW_EXTENDED=new RewriteRuleTokenStream(adaptor,"token KW_EXTENDED");
        RewriteRuleTokenStream stream_KW_EXPLAIN=new RewriteRuleTokenStream(adaptor,"token KW_EXPLAIN");
        RewriteRuleSubtreeStream stream_execStatement=new RewriteRuleSubtreeStream(adaptor,"rule execStatement");
        try {
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:136:2: ( KW_EXPLAIN (isExtended= KW_EXTENDED )? execStatement -> ^( TOK_EXPLAIN execStatement ( $isExtended)? ) )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:136:4: KW_EXPLAIN (isExtended= KW_EXTENDED )? execStatement
            {
            KW_EXPLAIN5=(Token)input.LT(1);
            match(input,KW_EXPLAIN,FOLLOW_KW_EXPLAIN_in_explainStatement387); if (failed) return retval;
            if ( backtracking==0 ) stream_KW_EXPLAIN.add(KW_EXPLAIN5);

            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:136:15: (isExtended= KW_EXTENDED )?
            int alt2=2;
            int LA2_0 = input.LA(1);

            if ( (LA2_0==KW_EXTENDED) ) {
                alt2=1;
            }
            switch (alt2) {
                case 1 :
                    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:136:16: isExtended= KW_EXTENDED
                    {
                    isExtended=(Token)input.LT(1);
                    match(input,KW_EXTENDED,FOLLOW_KW_EXTENDED_in_explainStatement392); if (failed) return retval;
                    if ( backtracking==0 ) stream_KW_EXTENDED.add(isExtended);


                    }
                    break;

            }

            pushFollow(FOLLOW_execStatement_in_explainStatement396);
            execStatement6=execStatement();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_execStatement.add(execStatement6.getTree());

            // AST REWRITE
            // elements: isExtended, execStatement
            // token labels: isExtended
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleTokenStream stream_isExtended=new RewriteRuleTokenStream(adaptor,"token isExtended",isExtended);
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 136:55: -> ^( TOK_EXPLAIN execStatement ( $isExtended)? )
            {
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:136:58: ^( TOK_EXPLAIN execStatement ( $isExtended)? )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(adaptor.create(TOK_EXPLAIN, "TOK_EXPLAIN"), root_1);

                adaptor.addChild(root_1, stream_execStatement.next());
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:136:86: ( $isExtended)?
                if ( stream_isExtended.hasNext() ) {
                    adaptor.addChild(root_1, stream_isExtended.next());

                }
                stream_isExtended.reset();

                adaptor.addChild(root_0, root_1);
                }

            }

            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e) {
          reportError(e);
          throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end explainStatement

    public static class execStatement_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start execStatement
    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:139:1: execStatement : ( queryStatementExpression | loadStatement | ddlStatement );
    public final execStatement_return execStatement() throws RecognitionException {
        execStatement_return retval = new execStatement_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        queryStatementExpression_return queryStatementExpression7 = null;

        loadStatement_return loadStatement8 = null;

        ddlStatement_return ddlStatement9 = null;



        try {
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:140:5: ( queryStatementExpression | loadStatement | ddlStatement )
            int alt3=3;
            switch ( input.LA(1) ) {
            case KW_INSERT:
            case KW_SELECT:
            case KW_FROM:
                {
                alt3=1;
                }
                break;
            case KW_LOAD:
                {
                alt3=2;
                }
                break;
            case KW_CREATE:
            case KW_DROP:
            case KW_ALTER:
            case KW_DESCRIBE:
            case KW_SHOW:
                {
                alt3=3;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("139:1: execStatement : ( queryStatementExpression | loadStatement | ddlStatement );", 3, 0, input);

                throw nvae;
            }

            switch (alt3) {
                case 1 :
                    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:140:7: queryStatementExpression
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_queryStatementExpression_in_execStatement424);
                    queryStatementExpression7=queryStatementExpression();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, queryStatementExpression7.getTree());

                    }
                    break;
                case 2 :
                    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:141:7: loadStatement
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_loadStatement_in_execStatement432);
                    loadStatement8=loadStatement();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, loadStatement8.getTree());

                    }
                    break;
                case 3 :
                    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:142:7: ddlStatement
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_ddlStatement_in_execStatement440);
                    ddlStatement9=ddlStatement();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, ddlStatement9.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e) {
          reportError(e);
          throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end execStatement

    public static class loadStatement_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start loadStatement
    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:145:1: loadStatement : KW_LOAD KW_DATA (islocal= KW_LOCAL )? KW_INPATH (path= StringLiteral ) (isoverwrite= KW_OVERWRITE )? KW_INTO KW_TABLE (tab= tabName ) -> ^( TOK_LOAD $path $tab ( $islocal)? ( $isoverwrite)? ) ;
    public final loadStatement_return loadStatement() throws RecognitionException {
        loadStatement_return retval = new loadStatement_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token islocal=null;
        Token path=null;
        Token isoverwrite=null;
        Token KW_LOAD10=null;
        Token KW_DATA11=null;
        Token KW_INPATH12=null;
        Token KW_INTO13=null;
        Token KW_TABLE14=null;
        tabName_return tab = null;


        CommonTree islocal_tree=null;
        CommonTree path_tree=null;
        CommonTree isoverwrite_tree=null;
        CommonTree KW_LOAD10_tree=null;
        CommonTree KW_DATA11_tree=null;
        CommonTree KW_INPATH12_tree=null;
        CommonTree KW_INTO13_tree=null;
        CommonTree KW_TABLE14_tree=null;
        RewriteRuleTokenStream stream_StringLiteral=new RewriteRuleTokenStream(adaptor,"token StringLiteral");
        RewriteRuleTokenStream stream_KW_INPATH=new RewriteRuleTokenStream(adaptor,"token KW_INPATH");
        RewriteRuleTokenStream stream_KW_INTO=new RewriteRuleTokenStream(adaptor,"token KW_INTO");
        RewriteRuleTokenStream stream_KW_LOCAL=new RewriteRuleTokenStream(adaptor,"token KW_LOCAL");
        RewriteRuleTokenStream stream_KW_OVERWRITE=new RewriteRuleTokenStream(adaptor,"token KW_OVERWRITE");
        RewriteRuleTokenStream stream_KW_TABLE=new RewriteRuleTokenStream(adaptor,"token KW_TABLE");
        RewriteRuleTokenStream stream_KW_LOAD=new RewriteRuleTokenStream(adaptor,"token KW_LOAD");
        RewriteRuleTokenStream stream_KW_DATA=new RewriteRuleTokenStream(adaptor,"token KW_DATA");
        RewriteRuleSubtreeStream stream_tabName=new RewriteRuleSubtreeStream(adaptor,"rule tabName");
        try {
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:146:5: ( KW_LOAD KW_DATA (islocal= KW_LOCAL )? KW_INPATH (path= StringLiteral ) (isoverwrite= KW_OVERWRITE )? KW_INTO KW_TABLE (tab= tabName ) -> ^( TOK_LOAD $path $tab ( $islocal)? ( $isoverwrite)? ) )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:146:7: KW_LOAD KW_DATA (islocal= KW_LOCAL )? KW_INPATH (path= StringLiteral ) (isoverwrite= KW_OVERWRITE )? KW_INTO KW_TABLE (tab= tabName )
            {
            KW_LOAD10=(Token)input.LT(1);
            match(input,KW_LOAD,FOLLOW_KW_LOAD_in_loadStatement457); if (failed) return retval;
            if ( backtracking==0 ) stream_KW_LOAD.add(KW_LOAD10);

            KW_DATA11=(Token)input.LT(1);
            match(input,KW_DATA,FOLLOW_KW_DATA_in_loadStatement459); if (failed) return retval;
            if ( backtracking==0 ) stream_KW_DATA.add(KW_DATA11);

            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:146:23: (islocal= KW_LOCAL )?
            int alt4=2;
            int LA4_0 = input.LA(1);

            if ( (LA4_0==KW_LOCAL) ) {
                alt4=1;
            }
            switch (alt4) {
                case 1 :
                    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:146:24: islocal= KW_LOCAL
                    {
                    islocal=(Token)input.LT(1);
                    match(input,KW_LOCAL,FOLLOW_KW_LOCAL_in_loadStatement464); if (failed) return retval;
                    if ( backtracking==0 ) stream_KW_LOCAL.add(islocal);


                    }
                    break;

            }

            KW_INPATH12=(Token)input.LT(1);
            match(input,KW_INPATH,FOLLOW_KW_INPATH_in_loadStatement468); if (failed) return retval;
            if ( backtracking==0 ) stream_KW_INPATH.add(KW_INPATH12);

            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:146:53: (path= StringLiteral )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:146:54: path= StringLiteral
            {
            path=(Token)input.LT(1);
            match(input,StringLiteral,FOLLOW_StringLiteral_in_loadStatement473); if (failed) return retval;
            if ( backtracking==0 ) stream_StringLiteral.add(path);


            }

            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:146:74: (isoverwrite= KW_OVERWRITE )?
            int alt5=2;
            int LA5_0 = input.LA(1);

            if ( (LA5_0==KW_OVERWRITE) ) {
                alt5=1;
            }
            switch (alt5) {
                case 1 :
                    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:146:75: isoverwrite= KW_OVERWRITE
                    {
                    isoverwrite=(Token)input.LT(1);
                    match(input,KW_OVERWRITE,FOLLOW_KW_OVERWRITE_in_loadStatement479); if (failed) return retval;
                    if ( backtracking==0 ) stream_KW_OVERWRITE.add(isoverwrite);


                    }
                    break;

            }

            KW_INTO13=(Token)input.LT(1);
            match(input,KW_INTO,FOLLOW_KW_INTO_in_loadStatement483); if (failed) return retval;
            if ( backtracking==0 ) stream_KW_INTO.add(KW_INTO13);

            KW_TABLE14=(Token)input.LT(1);
            match(input,KW_TABLE,FOLLOW_KW_TABLE_in_loadStatement485); if (failed) return retval;
            if ( backtracking==0 ) stream_KW_TABLE.add(KW_TABLE14);

            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:146:119: (tab= tabName )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:146:120: tab= tabName
            {
            pushFollow(FOLLOW_tabName_in_loadStatement490);
            tab=tabName();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_tabName.add(tab.getTree());

            }


            // AST REWRITE
            // elements: isoverwrite, islocal, path, tab
            // token labels: islocal, isoverwrite, path
            // rule labels: retval, tab
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleTokenStream stream_islocal=new RewriteRuleTokenStream(adaptor,"token islocal",islocal);
            RewriteRuleTokenStream stream_isoverwrite=new RewriteRuleTokenStream(adaptor,"token isoverwrite",isoverwrite);
            RewriteRuleTokenStream stream_path=new RewriteRuleTokenStream(adaptor,"token path",path);
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);
            RewriteRuleSubtreeStream stream_tab=new RewriteRuleSubtreeStream(adaptor,"token tab",tab!=null?tab.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 147:5: -> ^( TOK_LOAD $path $tab ( $islocal)? ( $isoverwrite)? )
            {
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:147:8: ^( TOK_LOAD $path $tab ( $islocal)? ( $isoverwrite)? )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(adaptor.create(TOK_LOAD, "TOK_LOAD"), root_1);

                adaptor.addChild(root_1, stream_path.next());
                adaptor.addChild(root_1, stream_tab.next());
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:147:30: ( $islocal)?
                if ( stream_islocal.hasNext() ) {
                    adaptor.addChild(root_1, stream_islocal.next());

                }
                stream_islocal.reset();
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:147:40: ( $isoverwrite)?
                if ( stream_isoverwrite.hasNext() ) {
                    adaptor.addChild(root_1, stream_isoverwrite.next());

                }
                stream_isoverwrite.reset();

                adaptor.addChild(root_0, root_1);
                }

            }

            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e) {
          reportError(e);
          throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end loadStatement

    public static class ddlStatement_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start ddlStatement
    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:150:1: ddlStatement : ( createStatement | dropStatement | alterStatement | descStatement | showStatement | createFunctionStatement );
    public final ddlStatement_return ddlStatement() throws RecognitionException {
        ddlStatement_return retval = new ddlStatement_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        createStatement_return createStatement15 = null;

        dropStatement_return dropStatement16 = null;

        alterStatement_return alterStatement17 = null;

        descStatement_return descStatement18 = null;

        showStatement_return showStatement19 = null;

        createFunctionStatement_return createFunctionStatement20 = null;



        try {
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:151:5: ( createStatement | dropStatement | alterStatement | descStatement | showStatement | createFunctionStatement )
            int alt6=6;
            switch ( input.LA(1) ) {
            case KW_CREATE:
                {
                int LA6_1 = input.LA(2);

                if ( (LA6_1==KW_TEMPORARY) ) {
                    alt6=6;
                }
                else if ( (LA6_1==KW_TABLE||LA6_1==KW_EXTERNAL) ) {
                    alt6=1;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("150:1: ddlStatement : ( createStatement | dropStatement | alterStatement | descStatement | showStatement | createFunctionStatement );", 6, 1, input);

                    throw nvae;
                }
                }
                break;
            case KW_DROP:
                {
                alt6=2;
                }
                break;
            case KW_ALTER:
                {
                alt6=3;
                }
                break;
            case KW_DESCRIBE:
                {
                alt6=4;
                }
                break;
            case KW_SHOW:
                {
                alt6=5;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("150:1: ddlStatement : ( createStatement | dropStatement | alterStatement | descStatement | showStatement | createFunctionStatement );", 6, 0, input);

                throw nvae;
            }

            switch (alt6) {
                case 1 :
                    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:151:7: createStatement
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_createStatement_in_ddlStatement533);
                    createStatement15=createStatement();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, createStatement15.getTree());

                    }
                    break;
                case 2 :
                    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:152:7: dropStatement
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_dropStatement_in_ddlStatement541);
                    dropStatement16=dropStatement();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, dropStatement16.getTree());

                    }
                    break;
                case 3 :
                    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:153:7: alterStatement
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_alterStatement_in_ddlStatement549);
                    alterStatement17=alterStatement();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, alterStatement17.getTree());

                    }
                    break;
                case 4 :
                    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:154:7: descStatement
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_descStatement_in_ddlStatement557);
                    descStatement18=descStatement();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, descStatement18.getTree());

                    }
                    break;
                case 5 :
                    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:155:7: showStatement
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_showStatement_in_ddlStatement565);
                    showStatement19=showStatement();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, showStatement19.getTree());

                    }
                    break;
                case 6 :
                    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:156:7: createFunctionStatement
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_createFunctionStatement_in_ddlStatement573);
                    createFunctionStatement20=createFunctionStatement();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, createFunctionStatement20.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e) {
          reportError(e);
          throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end ddlStatement

    public static class createStatement_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start createStatement
    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:159:1: createStatement : KW_CREATE (ext= KW_EXTERNAL )? KW_TABLE name= Identifier LPAREN columnNameTypeList RPAREN ( tableComment )? ( tablePartition )? ( tableBuckets )? ( tableRowFormat )? ( tableFileFormat )? ( tableLocation )? -> {$ext == null}? ^( TOK_CREATETABLE $name columnNameTypeList ( tableComment )? ( tablePartition )? ( tableBuckets )? ( tableRowFormat )? ( tableFileFormat )? ( tableLocation )? ) -> ^( TOK_CREATEEXTTABLE $name columnNameTypeList ( tableComment )? ( tablePartition )? ( tableBuckets )? ( tableRowFormat )? ( tableFileFormat )? ( tableLocation )? ) ;
    public final createStatement_return createStatement() throws RecognitionException {
        createStatement_return retval = new createStatement_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token ext=null;
        Token name=null;
        Token KW_CREATE21=null;
        Token KW_TABLE22=null;
        Token LPAREN23=null;
        Token RPAREN25=null;
        columnNameTypeList_return columnNameTypeList24 = null;

        tableComment_return tableComment26 = null;

        tablePartition_return tablePartition27 = null;

        tableBuckets_return tableBuckets28 = null;

        tableRowFormat_return tableRowFormat29 = null;

        tableFileFormat_return tableFileFormat30 = null;

        tableLocation_return tableLocation31 = null;


        CommonTree ext_tree=null;
        CommonTree name_tree=null;
        CommonTree KW_CREATE21_tree=null;
        CommonTree KW_TABLE22_tree=null;
        CommonTree LPAREN23_tree=null;
        CommonTree RPAREN25_tree=null;
        RewriteRuleTokenStream stream_KW_CREATE=new RewriteRuleTokenStream(adaptor,"token KW_CREATE");
        RewriteRuleTokenStream stream_RPAREN=new RewriteRuleTokenStream(adaptor,"token RPAREN");
        RewriteRuleTokenStream stream_KW_EXTERNAL=new RewriteRuleTokenStream(adaptor,"token KW_EXTERNAL");
        RewriteRuleTokenStream stream_KW_TABLE=new RewriteRuleTokenStream(adaptor,"token KW_TABLE");
        RewriteRuleTokenStream stream_Identifier=new RewriteRuleTokenStream(adaptor,"token Identifier");
        RewriteRuleTokenStream stream_LPAREN=new RewriteRuleTokenStream(adaptor,"token LPAREN");
        RewriteRuleSubtreeStream stream_columnNameTypeList=new RewriteRuleSubtreeStream(adaptor,"rule columnNameTypeList");
        RewriteRuleSubtreeStream stream_tableBuckets=new RewriteRuleSubtreeStream(adaptor,"rule tableBuckets");
        RewriteRuleSubtreeStream stream_tablePartition=new RewriteRuleSubtreeStream(adaptor,"rule tablePartition");
        RewriteRuleSubtreeStream stream_tableComment=new RewriteRuleSubtreeStream(adaptor,"rule tableComment");
        RewriteRuleSubtreeStream stream_tableRowFormat=new RewriteRuleSubtreeStream(adaptor,"rule tableRowFormat");
        RewriteRuleSubtreeStream stream_tableFileFormat=new RewriteRuleSubtreeStream(adaptor,"rule tableFileFormat");
        RewriteRuleSubtreeStream stream_tableLocation=new RewriteRuleSubtreeStream(adaptor,"rule tableLocation");
        try {
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:160:5: ( KW_CREATE (ext= KW_EXTERNAL )? KW_TABLE name= Identifier LPAREN columnNameTypeList RPAREN ( tableComment )? ( tablePartition )? ( tableBuckets )? ( tableRowFormat )? ( tableFileFormat )? ( tableLocation )? -> {$ext == null}? ^( TOK_CREATETABLE $name columnNameTypeList ( tableComment )? ( tablePartition )? ( tableBuckets )? ( tableRowFormat )? ( tableFileFormat )? ( tableLocation )? ) -> ^( TOK_CREATEEXTTABLE $name columnNameTypeList ( tableComment )? ( tablePartition )? ( tableBuckets )? ( tableRowFormat )? ( tableFileFormat )? ( tableLocation )? ) )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:160:7: KW_CREATE (ext= KW_EXTERNAL )? KW_TABLE name= Identifier LPAREN columnNameTypeList RPAREN ( tableComment )? ( tablePartition )? ( tableBuckets )? ( tableRowFormat )? ( tableFileFormat )? ( tableLocation )?
            {
            KW_CREATE21=(Token)input.LT(1);
            match(input,KW_CREATE,FOLLOW_KW_CREATE_in_createStatement590); if (failed) return retval;
            if ( backtracking==0 ) stream_KW_CREATE.add(KW_CREATE21);

            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:160:17: (ext= KW_EXTERNAL )?
            int alt7=2;
            int LA7_0 = input.LA(1);

            if ( (LA7_0==KW_EXTERNAL) ) {
                alt7=1;
            }
            switch (alt7) {
                case 1 :
                    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:160:18: ext= KW_EXTERNAL
                    {
                    ext=(Token)input.LT(1);
                    match(input,KW_EXTERNAL,FOLLOW_KW_EXTERNAL_in_createStatement595); if (failed) return retval;
                    if ( backtracking==0 ) stream_KW_EXTERNAL.add(ext);


                    }
                    break;

            }

            KW_TABLE22=(Token)input.LT(1);
            match(input,KW_TABLE,FOLLOW_KW_TABLE_in_createStatement599); if (failed) return retval;
            if ( backtracking==0 ) stream_KW_TABLE.add(KW_TABLE22);

            name=(Token)input.LT(1);
            match(input,Identifier,FOLLOW_Identifier_in_createStatement603); if (failed) return retval;
            if ( backtracking==0 ) stream_Identifier.add(name);

            LPAREN23=(Token)input.LT(1);
            match(input,LPAREN,FOLLOW_LPAREN_in_createStatement605); if (failed) return retval;
            if ( backtracking==0 ) stream_LPAREN.add(LPAREN23);

            pushFollow(FOLLOW_columnNameTypeList_in_createStatement607);
            columnNameTypeList24=columnNameTypeList();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_columnNameTypeList.add(columnNameTypeList24.getTree());
            RPAREN25=(Token)input.LT(1);
            match(input,RPAREN,FOLLOW_RPAREN_in_createStatement609); if (failed) return retval;
            if ( backtracking==0 ) stream_RPAREN.add(RPAREN25);

            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:160:94: ( tableComment )?
            int alt8=2;
            int LA8_0 = input.LA(1);

            if ( (LA8_0==KW_COMMENT) ) {
                alt8=1;
            }
            switch (alt8) {
                case 1 :
                    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:0:0: tableComment
                    {
                    pushFollow(FOLLOW_tableComment_in_createStatement611);
                    tableComment26=tableComment();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_tableComment.add(tableComment26.getTree());

                    }
                    break;

            }

            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:160:108: ( tablePartition )?
            int alt9=2;
            int LA9_0 = input.LA(1);

            if ( (LA9_0==KW_PARTITIONED) ) {
                alt9=1;
            }
            switch (alt9) {
                case 1 :
                    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:0:0: tablePartition
                    {
                    pushFollow(FOLLOW_tablePartition_in_createStatement614);
                    tablePartition27=tablePartition();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_tablePartition.add(tablePartition27.getTree());

                    }
                    break;

            }

            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:160:124: ( tableBuckets )?
            int alt10=2;
            int LA10_0 = input.LA(1);

            if ( (LA10_0==KW_CLUSTERED) ) {
                alt10=1;
            }
            switch (alt10) {
                case 1 :
                    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:0:0: tableBuckets
                    {
                    pushFollow(FOLLOW_tableBuckets_in_createStatement617);
                    tableBuckets28=tableBuckets();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_tableBuckets.add(tableBuckets28.getTree());

                    }
                    break;

            }

            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:160:138: ( tableRowFormat )?
            int alt11=2;
            int LA11_0 = input.LA(1);

            if ( (LA11_0==KW_ROW) ) {
                alt11=1;
            }
            switch (alt11) {
                case 1 :
                    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:0:0: tableRowFormat
                    {
                    pushFollow(FOLLOW_tableRowFormat_in_createStatement620);
                    tableRowFormat29=tableRowFormat();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_tableRowFormat.add(tableRowFormat29.getTree());

                    }
                    break;

            }

            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:160:154: ( tableFileFormat )?
            int alt12=2;
            int LA12_0 = input.LA(1);

            if ( (LA12_0==KW_STORED) ) {
                alt12=1;
            }
            switch (alt12) {
                case 1 :
                    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:0:0: tableFileFormat
                    {
                    pushFollow(FOLLOW_tableFileFormat_in_createStatement623);
                    tableFileFormat30=tableFileFormat();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_tableFileFormat.add(tableFileFormat30.getTree());

                    }
                    break;

            }

            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:160:171: ( tableLocation )?
            int alt13=2;
            int LA13_0 = input.LA(1);

            if ( (LA13_0==KW_LOCATION) ) {
                alt13=1;
            }
            switch (alt13) {
                case 1 :
                    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:0:0: tableLocation
                    {
                    pushFollow(FOLLOW_tableLocation_in_createStatement626);
                    tableLocation31=tableLocation();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_tableLocation.add(tableLocation31.getTree());

                    }
                    break;

            }


            // AST REWRITE
            // elements: tableBuckets, tableFileFormat, tableRowFormat, tableComment, name, columnNameTypeList, tableComment, tablePartition, tableLocation, tableFileFormat, tableRowFormat, tableLocation, tablePartition, name, columnNameTypeList, tableBuckets
            // token labels: name
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleTokenStream stream_name=new RewriteRuleTokenStream(adaptor,"token name",name);
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 161:5: -> {$ext == null}? ^( TOK_CREATETABLE $name columnNameTypeList ( tableComment )? ( tablePartition )? ( tableBuckets )? ( tableRowFormat )? ( tableFileFormat )? ( tableLocation )? )
            if (ext == null) {
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:161:24: ^( TOK_CREATETABLE $name columnNameTypeList ( tableComment )? ( tablePartition )? ( tableBuckets )? ( tableRowFormat )? ( tableFileFormat )? ( tableLocation )? )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(adaptor.create(TOK_CREATETABLE, "TOK_CREATETABLE"), root_1);

                adaptor.addChild(root_1, stream_name.next());
                adaptor.addChild(root_1, stream_columnNameTypeList.next());
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:161:67: ( tableComment )?
                if ( stream_tableComment.hasNext() ) {
                    adaptor.addChild(root_1, stream_tableComment.next());

                }
                stream_tableComment.reset();
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:161:81: ( tablePartition )?
                if ( stream_tablePartition.hasNext() ) {
                    adaptor.addChild(root_1, stream_tablePartition.next());

                }
                stream_tablePartition.reset();
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:161:97: ( tableBuckets )?
                if ( stream_tableBuckets.hasNext() ) {
                    adaptor.addChild(root_1, stream_tableBuckets.next());

                }
                stream_tableBuckets.reset();
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:161:111: ( tableRowFormat )?
                if ( stream_tableRowFormat.hasNext() ) {
                    adaptor.addChild(root_1, stream_tableRowFormat.next());

                }
                stream_tableRowFormat.reset();
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:161:127: ( tableFileFormat )?
                if ( stream_tableFileFormat.hasNext() ) {
                    adaptor.addChild(root_1, stream_tableFileFormat.next());

                }
                stream_tableFileFormat.reset();
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:161:144: ( tableLocation )?
                if ( stream_tableLocation.hasNext() ) {
                    adaptor.addChild(root_1, stream_tableLocation.next());

                }
                stream_tableLocation.reset();

                adaptor.addChild(root_0, root_1);
                }

            }
            else // 162:5: -> ^( TOK_CREATEEXTTABLE $name columnNameTypeList ( tableComment )? ( tablePartition )? ( tableBuckets )? ( tableRowFormat )? ( tableFileFormat )? ( tableLocation )? )
            {
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:162:24: ^( TOK_CREATEEXTTABLE $name columnNameTypeList ( tableComment )? ( tablePartition )? ( tableBuckets )? ( tableRowFormat )? ( tableFileFormat )? ( tableLocation )? )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(adaptor.create(TOK_CREATEEXTTABLE, "TOK_CREATEEXTTABLE"), root_1);

                adaptor.addChild(root_1, stream_name.next());
                adaptor.addChild(root_1, stream_columnNameTypeList.next());
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:162:70: ( tableComment )?
                if ( stream_tableComment.hasNext() ) {
                    adaptor.addChild(root_1, stream_tableComment.next());

                }
                stream_tableComment.reset();
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:162:84: ( tablePartition )?
                if ( stream_tablePartition.hasNext() ) {
                    adaptor.addChild(root_1, stream_tablePartition.next());

                }
                stream_tablePartition.reset();
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:162:100: ( tableBuckets )?
                if ( stream_tableBuckets.hasNext() ) {
                    adaptor.addChild(root_1, stream_tableBuckets.next());

                }
                stream_tableBuckets.reset();
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:162:114: ( tableRowFormat )?
                if ( stream_tableRowFormat.hasNext() ) {
                    adaptor.addChild(root_1, stream_tableRowFormat.next());

                }
                stream_tableRowFormat.reset();
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:162:130: ( tableFileFormat )?
                if ( stream_tableFileFormat.hasNext() ) {
                    adaptor.addChild(root_1, stream_tableFileFormat.next());

                }
                stream_tableFileFormat.reset();
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:162:147: ( tableLocation )?
                if ( stream_tableLocation.hasNext() ) {
                    adaptor.addChild(root_1, stream_tableLocation.next());

                }
                stream_tableLocation.reset();

                adaptor.addChild(root_0, root_1);
                }

            }

            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e) {
          reportError(e);
          throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end createStatement

    public static class dropStatement_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start dropStatement
    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:165:1: dropStatement : KW_DROP KW_TABLE Identifier -> ^( TOK_DROPTABLE Identifier ) ;
    public final dropStatement_return dropStatement() throws RecognitionException {
        dropStatement_return retval = new dropStatement_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token KW_DROP32=null;
        Token KW_TABLE33=null;
        Token Identifier34=null;

        CommonTree KW_DROP32_tree=null;
        CommonTree KW_TABLE33_tree=null;
        CommonTree Identifier34_tree=null;
        RewriteRuleTokenStream stream_KW_DROP=new RewriteRuleTokenStream(adaptor,"token KW_DROP");
        RewriteRuleTokenStream stream_KW_TABLE=new RewriteRuleTokenStream(adaptor,"token KW_TABLE");
        RewriteRuleTokenStream stream_Identifier=new RewriteRuleTokenStream(adaptor,"token Identifier");

        try {
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:166:5: ( KW_DROP KW_TABLE Identifier -> ^( TOK_DROPTABLE Identifier ) )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:166:7: KW_DROP KW_TABLE Identifier
            {
            KW_DROP32=(Token)input.LT(1);
            match(input,KW_DROP,FOLLOW_KW_DROP_in_dropStatement728); if (failed) return retval;
            if ( backtracking==0 ) stream_KW_DROP.add(KW_DROP32);

            KW_TABLE33=(Token)input.LT(1);
            match(input,KW_TABLE,FOLLOW_KW_TABLE_in_dropStatement730); if (failed) return retval;
            if ( backtracking==0 ) stream_KW_TABLE.add(KW_TABLE33);

            Identifier34=(Token)input.LT(1);
            match(input,Identifier,FOLLOW_Identifier_in_dropStatement732); if (failed) return retval;
            if ( backtracking==0 ) stream_Identifier.add(Identifier34);


            // AST REWRITE
            // elements: Identifier
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 166:36: -> ^( TOK_DROPTABLE Identifier )
            {
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:166:39: ^( TOK_DROPTABLE Identifier )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(adaptor.create(TOK_DROPTABLE, "TOK_DROPTABLE"), root_1);

                adaptor.addChild(root_1, stream_Identifier.next());

                adaptor.addChild(root_0, root_1);
                }

            }

            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e) {
          reportError(e);
          throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end dropStatement

    public static class alterStatement_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start alterStatement
    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:169:1: alterStatement : ( alterStatementRename | alterStatementAddCol | alterStatementDropPartitions );
    public final alterStatement_return alterStatement() throws RecognitionException {
        alterStatement_return retval = new alterStatement_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        alterStatementRename_return alterStatementRename35 = null;

        alterStatementAddCol_return alterStatementAddCol36 = null;

        alterStatementDropPartitions_return alterStatementDropPartitions37 = null;



        try {
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:170:5: ( alterStatementRename | alterStatementAddCol | alterStatementDropPartitions )
            int alt14=3;
            int LA14_0 = input.LA(1);

            if ( (LA14_0==KW_ALTER) ) {
                int LA14_1 = input.LA(2);

                if ( (LA14_1==KW_TABLE) ) {
                    int LA14_2 = input.LA(3);

                    if ( (LA14_2==Identifier) ) {
                        switch ( input.LA(4) ) {
                        case KW_RENAME:
                            {
                            alt14=1;
                            }
                            break;
                        case KW_DROP:
                            {
                            alt14=3;
                            }
                            break;
                        case KW_ADD:
                        case KW_REPLACE:
                            {
                            alt14=2;
                            }
                            break;
                        default:
                            if (backtracking>0) {failed=true; return retval;}
                            NoViableAltException nvae =
                                new NoViableAltException("169:1: alterStatement : ( alterStatementRename | alterStatementAddCol | alterStatementDropPartitions );", 14, 3, input);

                            throw nvae;
                        }

                    }
                    else {
                        if (backtracking>0) {failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("169:1: alterStatement : ( alterStatementRename | alterStatementAddCol | alterStatementDropPartitions );", 14, 2, input);

                        throw nvae;
                    }
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("169:1: alterStatement : ( alterStatementRename | alterStatementAddCol | alterStatementDropPartitions );", 14, 1, input);

                    throw nvae;
                }
            }
            else {
                if (backtracking>0) {failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("169:1: alterStatement : ( alterStatementRename | alterStatementAddCol | alterStatementDropPartitions );", 14, 0, input);

                throw nvae;
            }
            switch (alt14) {
                case 1 :
                    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:170:7: alterStatementRename
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_alterStatementRename_in_alterStatement758);
                    alterStatementRename35=alterStatementRename();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, alterStatementRename35.getTree());

                    }
                    break;
                case 2 :
                    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:171:7: alterStatementAddCol
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_alterStatementAddCol_in_alterStatement766);
                    alterStatementAddCol36=alterStatementAddCol();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, alterStatementAddCol36.getTree());

                    }
                    break;
                case 3 :
                    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:172:7: alterStatementDropPartitions
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_alterStatementDropPartitions_in_alterStatement774);
                    alterStatementDropPartitions37=alterStatementDropPartitions();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, alterStatementDropPartitions37.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e) {
          reportError(e);
          throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end alterStatement

    public static class alterStatementRename_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start alterStatementRename
    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:175:1: alterStatementRename : KW_ALTER KW_TABLE oldName= Identifier KW_RENAME KW_TO newName= Identifier -> ^( TOK_ALTERTABLE_RENAME $oldName $newName) ;
    public final alterStatementRename_return alterStatementRename() throws RecognitionException {
        alterStatementRename_return retval = new alterStatementRename_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token oldName=null;
        Token newName=null;
        Token KW_ALTER38=null;
        Token KW_TABLE39=null;
        Token KW_RENAME40=null;
        Token KW_TO41=null;

        CommonTree oldName_tree=null;
        CommonTree newName_tree=null;
        CommonTree KW_ALTER38_tree=null;
        CommonTree KW_TABLE39_tree=null;
        CommonTree KW_RENAME40_tree=null;
        CommonTree KW_TO41_tree=null;
        RewriteRuleTokenStream stream_KW_ALTER=new RewriteRuleTokenStream(adaptor,"token KW_ALTER");
        RewriteRuleTokenStream stream_KW_RENAME=new RewriteRuleTokenStream(adaptor,"token KW_RENAME");
        RewriteRuleTokenStream stream_KW_TABLE=new RewriteRuleTokenStream(adaptor,"token KW_TABLE");
        RewriteRuleTokenStream stream_Identifier=new RewriteRuleTokenStream(adaptor,"token Identifier");
        RewriteRuleTokenStream stream_KW_TO=new RewriteRuleTokenStream(adaptor,"token KW_TO");

        try {
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:176:5: ( KW_ALTER KW_TABLE oldName= Identifier KW_RENAME KW_TO newName= Identifier -> ^( TOK_ALTERTABLE_RENAME $oldName $newName) )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:176:7: KW_ALTER KW_TABLE oldName= Identifier KW_RENAME KW_TO newName= Identifier
            {
            KW_ALTER38=(Token)input.LT(1);
            match(input,KW_ALTER,FOLLOW_KW_ALTER_in_alterStatementRename791); if (failed) return retval;
            if ( backtracking==0 ) stream_KW_ALTER.add(KW_ALTER38);

            KW_TABLE39=(Token)input.LT(1);
            match(input,KW_TABLE,FOLLOW_KW_TABLE_in_alterStatementRename793); if (failed) return retval;
            if ( backtracking==0 ) stream_KW_TABLE.add(KW_TABLE39);

            oldName=(Token)input.LT(1);
            match(input,Identifier,FOLLOW_Identifier_in_alterStatementRename797); if (failed) return retval;
            if ( backtracking==0 ) stream_Identifier.add(oldName);

            KW_RENAME40=(Token)input.LT(1);
            match(input,KW_RENAME,FOLLOW_KW_RENAME_in_alterStatementRename799); if (failed) return retval;
            if ( backtracking==0 ) stream_KW_RENAME.add(KW_RENAME40);

            KW_TO41=(Token)input.LT(1);
            match(input,KW_TO,FOLLOW_KW_TO_in_alterStatementRename801); if (failed) return retval;
            if ( backtracking==0 ) stream_KW_TO.add(KW_TO41);

            newName=(Token)input.LT(1);
            match(input,Identifier,FOLLOW_Identifier_in_alterStatementRename805); if (failed) return retval;
            if ( backtracking==0 ) stream_Identifier.add(newName);


            // AST REWRITE
            // elements: oldName, newName
            // token labels: newName, oldName
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleTokenStream stream_newName=new RewriteRuleTokenStream(adaptor,"token newName",newName);
            RewriteRuleTokenStream stream_oldName=new RewriteRuleTokenStream(adaptor,"token oldName",oldName);
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 177:5: -> ^( TOK_ALTERTABLE_RENAME $oldName $newName)
            {
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:177:8: ^( TOK_ALTERTABLE_RENAME $oldName $newName)
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(adaptor.create(TOK_ALTERTABLE_RENAME, "TOK_ALTERTABLE_RENAME"), root_1);

                adaptor.addChild(root_1, stream_oldName.next());
                adaptor.addChild(root_1, stream_newName.next());

                adaptor.addChild(root_0, root_1);
                }

            }

            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e) {
          reportError(e);
          throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end alterStatementRename

    public static class alterStatementAddCol_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start alterStatementAddCol
    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:180:1: alterStatementAddCol : KW_ALTER KW_TABLE Identifier (add= KW_ADD | replace= KW_REPLACE ) KW_COLUMNS LPAREN columnNameTypeList RPAREN -> {$add != null}? ^( TOK_ALTERTABLE_ADDCOLS Identifier columnNameTypeList ) -> ^( TOK_ALTERTABLE_REPLACECOLS Identifier columnNameTypeList ) ;
    public final alterStatementAddCol_return alterStatementAddCol() throws RecognitionException {
        alterStatementAddCol_return retval = new alterStatementAddCol_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token add=null;
        Token replace=null;
        Token KW_ALTER42=null;
        Token KW_TABLE43=null;
        Token Identifier44=null;
        Token KW_COLUMNS45=null;
        Token LPAREN46=null;
        Token RPAREN48=null;
        columnNameTypeList_return columnNameTypeList47 = null;


        CommonTree add_tree=null;
        CommonTree replace_tree=null;
        CommonTree KW_ALTER42_tree=null;
        CommonTree KW_TABLE43_tree=null;
        CommonTree Identifier44_tree=null;
        CommonTree KW_COLUMNS45_tree=null;
        CommonTree LPAREN46_tree=null;
        CommonTree RPAREN48_tree=null;
        RewriteRuleTokenStream stream_KW_ALTER=new RewriteRuleTokenStream(adaptor,"token KW_ALTER");
        RewriteRuleTokenStream stream_RPAREN=new RewriteRuleTokenStream(adaptor,"token RPAREN");
        RewriteRuleTokenStream stream_KW_REPLACE=new RewriteRuleTokenStream(adaptor,"token KW_REPLACE");
        RewriteRuleTokenStream stream_KW_COLUMNS=new RewriteRuleTokenStream(adaptor,"token KW_COLUMNS");
        RewriteRuleTokenStream stream_KW_TABLE=new RewriteRuleTokenStream(adaptor,"token KW_TABLE");
        RewriteRuleTokenStream stream_Identifier=new RewriteRuleTokenStream(adaptor,"token Identifier");
        RewriteRuleTokenStream stream_LPAREN=new RewriteRuleTokenStream(adaptor,"token LPAREN");
        RewriteRuleTokenStream stream_KW_ADD=new RewriteRuleTokenStream(adaptor,"token KW_ADD");
        RewriteRuleSubtreeStream stream_columnNameTypeList=new RewriteRuleSubtreeStream(adaptor,"rule columnNameTypeList");
        try {
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:181:5: ( KW_ALTER KW_TABLE Identifier (add= KW_ADD | replace= KW_REPLACE ) KW_COLUMNS LPAREN columnNameTypeList RPAREN -> {$add != null}? ^( TOK_ALTERTABLE_ADDCOLS Identifier columnNameTypeList ) -> ^( TOK_ALTERTABLE_REPLACECOLS Identifier columnNameTypeList ) )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:181:7: KW_ALTER KW_TABLE Identifier (add= KW_ADD | replace= KW_REPLACE ) KW_COLUMNS LPAREN columnNameTypeList RPAREN
            {
            KW_ALTER42=(Token)input.LT(1);
            match(input,KW_ALTER,FOLLOW_KW_ALTER_in_alterStatementAddCol839); if (failed) return retval;
            if ( backtracking==0 ) stream_KW_ALTER.add(KW_ALTER42);

            KW_TABLE43=(Token)input.LT(1);
            match(input,KW_TABLE,FOLLOW_KW_TABLE_in_alterStatementAddCol841); if (failed) return retval;
            if ( backtracking==0 ) stream_KW_TABLE.add(KW_TABLE43);

            Identifier44=(Token)input.LT(1);
            match(input,Identifier,FOLLOW_Identifier_in_alterStatementAddCol843); if (failed) return retval;
            if ( backtracking==0 ) stream_Identifier.add(Identifier44);

            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:181:36: (add= KW_ADD | replace= KW_REPLACE )
            int alt15=2;
            int LA15_0 = input.LA(1);

            if ( (LA15_0==KW_ADD) ) {
                alt15=1;
            }
            else if ( (LA15_0==KW_REPLACE) ) {
                alt15=2;
            }
            else {
                if (backtracking>0) {failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("181:36: (add= KW_ADD | replace= KW_REPLACE )", 15, 0, input);

                throw nvae;
            }
            switch (alt15) {
                case 1 :
                    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:181:37: add= KW_ADD
                    {
                    add=(Token)input.LT(1);
                    match(input,KW_ADD,FOLLOW_KW_ADD_in_alterStatementAddCol848); if (failed) return retval;
                    if ( backtracking==0 ) stream_KW_ADD.add(add);


                    }
                    break;
                case 2 :
                    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:181:50: replace= KW_REPLACE
                    {
                    replace=(Token)input.LT(1);
                    match(input,KW_REPLACE,FOLLOW_KW_REPLACE_in_alterStatementAddCol854); if (failed) return retval;
                    if ( backtracking==0 ) stream_KW_REPLACE.add(replace);


                    }
                    break;

            }

            KW_COLUMNS45=(Token)input.LT(1);
            match(input,KW_COLUMNS,FOLLOW_KW_COLUMNS_in_alterStatementAddCol857); if (failed) return retval;
            if ( backtracking==0 ) stream_KW_COLUMNS.add(KW_COLUMNS45);

            LPAREN46=(Token)input.LT(1);
            match(input,LPAREN,FOLLOW_LPAREN_in_alterStatementAddCol859); if (failed) return retval;
            if ( backtracking==0 ) stream_LPAREN.add(LPAREN46);

            pushFollow(FOLLOW_columnNameTypeList_in_alterStatementAddCol861);
            columnNameTypeList47=columnNameTypeList();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_columnNameTypeList.add(columnNameTypeList47.getTree());
            RPAREN48=(Token)input.LT(1);
            match(input,RPAREN,FOLLOW_RPAREN_in_alterStatementAddCol863); if (failed) return retval;
            if ( backtracking==0 ) stream_RPAREN.add(RPAREN48);


            // AST REWRITE
            // elements: Identifier, columnNameTypeList, Identifier, columnNameTypeList
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 182:5: -> {$add != null}? ^( TOK_ALTERTABLE_ADDCOLS Identifier columnNameTypeList )
            if (add != null) {
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:182:24: ^( TOK_ALTERTABLE_ADDCOLS Identifier columnNameTypeList )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(adaptor.create(TOK_ALTERTABLE_ADDCOLS, "TOK_ALTERTABLE_ADDCOLS"), root_1);

                adaptor.addChild(root_1, stream_Identifier.next());
                adaptor.addChild(root_1, stream_columnNameTypeList.next());

                adaptor.addChild(root_0, root_1);
                }

            }
            else // 183:5: -> ^( TOK_ALTERTABLE_REPLACECOLS Identifier columnNameTypeList )
            {
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:183:24: ^( TOK_ALTERTABLE_REPLACECOLS Identifier columnNameTypeList )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(adaptor.create(TOK_ALTERTABLE_REPLACECOLS, "TOK_ALTERTABLE_REPLACECOLS"), root_1);

                adaptor.addChild(root_1, stream_Identifier.next());
                adaptor.addChild(root_1, stream_columnNameTypeList.next());

                adaptor.addChild(root_0, root_1);
                }

            }

            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e) {
          reportError(e);
          throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end alterStatementAddCol

    public static class alterStatementDropPartitions_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start alterStatementDropPartitions
    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:186:1: alterStatementDropPartitions : KW_ALTER KW_TABLE Identifier KW_DROP partitionSpec ( COMMA partitionSpec )* -> ^( TOK_ALTERTABLE_DROPPARTS Identifier ( partitionSpec )+ ) ;
    public final alterStatementDropPartitions_return alterStatementDropPartitions() throws RecognitionException {
        alterStatementDropPartitions_return retval = new alterStatementDropPartitions_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token KW_ALTER49=null;
        Token KW_TABLE50=null;
        Token Identifier51=null;
        Token KW_DROP52=null;
        Token COMMA54=null;
        partitionSpec_return partitionSpec53 = null;

        partitionSpec_return partitionSpec55 = null;


        CommonTree KW_ALTER49_tree=null;
        CommonTree KW_TABLE50_tree=null;
        CommonTree Identifier51_tree=null;
        CommonTree KW_DROP52_tree=null;
        CommonTree COMMA54_tree=null;
        RewriteRuleTokenStream stream_KW_ALTER=new RewriteRuleTokenStream(adaptor,"token KW_ALTER");
        RewriteRuleTokenStream stream_KW_DROP=new RewriteRuleTokenStream(adaptor,"token KW_DROP");
        RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
        RewriteRuleTokenStream stream_KW_TABLE=new RewriteRuleTokenStream(adaptor,"token KW_TABLE");
        RewriteRuleTokenStream stream_Identifier=new RewriteRuleTokenStream(adaptor,"token Identifier");
        RewriteRuleSubtreeStream stream_partitionSpec=new RewriteRuleSubtreeStream(adaptor,"rule partitionSpec");
        try {
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:187:5: ( KW_ALTER KW_TABLE Identifier KW_DROP partitionSpec ( COMMA partitionSpec )* -> ^( TOK_ALTERTABLE_DROPPARTS Identifier ( partitionSpec )+ ) )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:187:7: KW_ALTER KW_TABLE Identifier KW_DROP partitionSpec ( COMMA partitionSpec )*
            {
            KW_ALTER49=(Token)input.LT(1);
            match(input,KW_ALTER,FOLLOW_KW_ALTER_in_alterStatementDropPartitions926); if (failed) return retval;
            if ( backtracking==0 ) stream_KW_ALTER.add(KW_ALTER49);

            KW_TABLE50=(Token)input.LT(1);
            match(input,KW_TABLE,FOLLOW_KW_TABLE_in_alterStatementDropPartitions928); if (failed) return retval;
            if ( backtracking==0 ) stream_KW_TABLE.add(KW_TABLE50);

            Identifier51=(Token)input.LT(1);
            match(input,Identifier,FOLLOW_Identifier_in_alterStatementDropPartitions930); if (failed) return retval;
            if ( backtracking==0 ) stream_Identifier.add(Identifier51);

            KW_DROP52=(Token)input.LT(1);
            match(input,KW_DROP,FOLLOW_KW_DROP_in_alterStatementDropPartitions932); if (failed) return retval;
            if ( backtracking==0 ) stream_KW_DROP.add(KW_DROP52);

            pushFollow(FOLLOW_partitionSpec_in_alterStatementDropPartitions934);
            partitionSpec53=partitionSpec();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_partitionSpec.add(partitionSpec53.getTree());
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:187:58: ( COMMA partitionSpec )*
            loop16:
            do {
                int alt16=2;
                int LA16_0 = input.LA(1);

                if ( (LA16_0==COMMA) ) {
                    alt16=1;
                }


                switch (alt16) {
            	case 1 :
            	    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:187:59: COMMA partitionSpec
            	    {
            	    COMMA54=(Token)input.LT(1);
            	    match(input,COMMA,FOLLOW_COMMA_in_alterStatementDropPartitions937); if (failed) return retval;
            	    if ( backtracking==0 ) stream_COMMA.add(COMMA54);

            	    pushFollow(FOLLOW_partitionSpec_in_alterStatementDropPartitions939);
            	    partitionSpec55=partitionSpec();
            	    _fsp--;
            	    if (failed) return retval;
            	    if ( backtracking==0 ) stream_partitionSpec.add(partitionSpec55.getTree());

            	    }
            	    break;

            	default :
            	    break loop16;
                }
            } while (true);


            // AST REWRITE
            // elements: Identifier, partitionSpec
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 188:5: -> ^( TOK_ALTERTABLE_DROPPARTS Identifier ( partitionSpec )+ )
            {
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:188:8: ^( TOK_ALTERTABLE_DROPPARTS Identifier ( partitionSpec )+ )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(adaptor.create(TOK_ALTERTABLE_DROPPARTS, "TOK_ALTERTABLE_DROPPARTS"), root_1);

                adaptor.addChild(root_1, stream_Identifier.next());
                if ( !(stream_partitionSpec.hasNext()) ) {
                    throw new RewriteEarlyExitException();
                }
                while ( stream_partitionSpec.hasNext() ) {
                    adaptor.addChild(root_1, stream_partitionSpec.next());

                }
                stream_partitionSpec.reset();

                adaptor.addChild(root_0, root_1);
                }

            }

            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e) {
          reportError(e);
          throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end alterStatementDropPartitions

    public static class descStatement_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start descStatement
    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:191:1: descStatement : KW_DESCRIBE (isExtended= KW_EXTENDED )? (tab= tabName ) -> ^( TOK_DESCTABLE $tab ( $isExtended)? ) ;
    public final descStatement_return descStatement() throws RecognitionException {
        descStatement_return retval = new descStatement_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token isExtended=null;
        Token KW_DESCRIBE56=null;
        tabName_return tab = null;


        CommonTree isExtended_tree=null;
        CommonTree KW_DESCRIBE56_tree=null;
        RewriteRuleTokenStream stream_KW_EXTENDED=new RewriteRuleTokenStream(adaptor,"token KW_EXTENDED");
        RewriteRuleTokenStream stream_KW_DESCRIBE=new RewriteRuleTokenStream(adaptor,"token KW_DESCRIBE");
        RewriteRuleSubtreeStream stream_tabName=new RewriteRuleSubtreeStream(adaptor,"rule tabName");
        try {
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:192:5: ( KW_DESCRIBE (isExtended= KW_EXTENDED )? (tab= tabName ) -> ^( TOK_DESCTABLE $tab ( $isExtended)? ) )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:192:7: KW_DESCRIBE (isExtended= KW_EXTENDED )? (tab= tabName )
            {
            KW_DESCRIBE56=(Token)input.LT(1);
            match(input,KW_DESCRIBE,FOLLOW_KW_DESCRIBE_in_descStatement973); if (failed) return retval;
            if ( backtracking==0 ) stream_KW_DESCRIBE.add(KW_DESCRIBE56);

            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:192:19: (isExtended= KW_EXTENDED )?
            int alt17=2;
            int LA17_0 = input.LA(1);

            if ( (LA17_0==KW_EXTENDED) ) {
                alt17=1;
            }
            switch (alt17) {
                case 1 :
                    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:192:20: isExtended= KW_EXTENDED
                    {
                    isExtended=(Token)input.LT(1);
                    match(input,KW_EXTENDED,FOLLOW_KW_EXTENDED_in_descStatement978); if (failed) return retval;
                    if ( backtracking==0 ) stream_KW_EXTENDED.add(isExtended);


                    }
                    break;

            }

            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:192:45: (tab= tabName )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:192:46: tab= tabName
            {
            pushFollow(FOLLOW_tabName_in_descStatement985);
            tab=tabName();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_tabName.add(tab.getTree());

            }


            // AST REWRITE
            // elements: isExtended, tab
            // token labels: isExtended
            // rule labels: retval, tab
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleTokenStream stream_isExtended=new RewriteRuleTokenStream(adaptor,"token isExtended",isExtended);
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);
            RewriteRuleSubtreeStream stream_tab=new RewriteRuleSubtreeStream(adaptor,"token tab",tab!=null?tab.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 192:60: -> ^( TOK_DESCTABLE $tab ( $isExtended)? )
            {
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:192:63: ^( TOK_DESCTABLE $tab ( $isExtended)? )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(adaptor.create(TOK_DESCTABLE, "TOK_DESCTABLE"), root_1);

                adaptor.addChild(root_1, stream_tab.next());
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:192:84: ( $isExtended)?
                if ( stream_isExtended.hasNext() ) {
                    adaptor.addChild(root_1, stream_isExtended.next());

                }
                stream_isExtended.reset();

                adaptor.addChild(root_0, root_1);
                }

            }

            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e) {
          reportError(e);
          throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end descStatement

    public static class showStatement_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start showStatement
    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:195:1: showStatement : ( KW_SHOW KW_TABLES ( showStmtIdentifier )? -> ^( TOK_SHOWTABLES ( showStmtIdentifier )? ) | KW_SHOW KW_PARTITIONS Identifier -> ^( TOK_SHOWPARTITIONS Identifier ) );
    public final showStatement_return showStatement() throws RecognitionException {
        showStatement_return retval = new showStatement_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token KW_SHOW57=null;
        Token KW_TABLES58=null;
        Token KW_SHOW60=null;
        Token KW_PARTITIONS61=null;
        Token Identifier62=null;
        showStmtIdentifier_return showStmtIdentifier59 = null;


        CommonTree KW_SHOW57_tree=null;
        CommonTree KW_TABLES58_tree=null;
        CommonTree KW_SHOW60_tree=null;
        CommonTree KW_PARTITIONS61_tree=null;
        CommonTree Identifier62_tree=null;
        RewriteRuleTokenStream stream_KW_SHOW=new RewriteRuleTokenStream(adaptor,"token KW_SHOW");
        RewriteRuleTokenStream stream_KW_TABLES=new RewriteRuleTokenStream(adaptor,"token KW_TABLES");
        RewriteRuleTokenStream stream_KW_PARTITIONS=new RewriteRuleTokenStream(adaptor,"token KW_PARTITIONS");
        RewriteRuleTokenStream stream_Identifier=new RewriteRuleTokenStream(adaptor,"token Identifier");
        RewriteRuleSubtreeStream stream_showStmtIdentifier=new RewriteRuleSubtreeStream(adaptor,"rule showStmtIdentifier");
        try {
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:196:5: ( KW_SHOW KW_TABLES ( showStmtIdentifier )? -> ^( TOK_SHOWTABLES ( showStmtIdentifier )? ) | KW_SHOW KW_PARTITIONS Identifier -> ^( TOK_SHOWPARTITIONS Identifier ) )
            int alt19=2;
            int LA19_0 = input.LA(1);

            if ( (LA19_0==KW_SHOW) ) {
                int LA19_1 = input.LA(2);

                if ( (LA19_1==KW_PARTITIONS) ) {
                    alt19=2;
                }
                else if ( (LA19_1==KW_TABLES) ) {
                    alt19=1;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("195:1: showStatement : ( KW_SHOW KW_TABLES ( showStmtIdentifier )? -> ^( TOK_SHOWTABLES ( showStmtIdentifier )? ) | KW_SHOW KW_PARTITIONS Identifier -> ^( TOK_SHOWPARTITIONS Identifier ) );", 19, 1, input);

                    throw nvae;
                }
            }
            else {
                if (backtracking>0) {failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("195:1: showStatement : ( KW_SHOW KW_TABLES ( showStmtIdentifier )? -> ^( TOK_SHOWTABLES ( showStmtIdentifier )? ) | KW_SHOW KW_PARTITIONS Identifier -> ^( TOK_SHOWPARTITIONS Identifier ) );", 19, 0, input);

                throw nvae;
            }
            switch (alt19) {
                case 1 :
                    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:196:7: KW_SHOW KW_TABLES ( showStmtIdentifier )?
                    {
                    KW_SHOW57=(Token)input.LT(1);
                    match(input,KW_SHOW,FOLLOW_KW_SHOW_in_showStatement1017); if (failed) return retval;
                    if ( backtracking==0 ) stream_KW_SHOW.add(KW_SHOW57);

                    KW_TABLES58=(Token)input.LT(1);
                    match(input,KW_TABLES,FOLLOW_KW_TABLES_in_showStatement1019); if (failed) return retval;
                    if ( backtracking==0 ) stream_KW_TABLES.add(KW_TABLES58);

                    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:196:25: ( showStmtIdentifier )?
                    int alt18=2;
                    int LA18_0 = input.LA(1);

                    if ( (LA18_0==StringLiteral||LA18_0==Identifier) ) {
                        alt18=1;
                    }
                    switch (alt18) {
                        case 1 :
                            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:0:0: showStmtIdentifier
                            {
                            pushFollow(FOLLOW_showStmtIdentifier_in_showStatement1021);
                            showStmtIdentifier59=showStmtIdentifier();
                            _fsp--;
                            if (failed) return retval;
                            if ( backtracking==0 ) stream_showStmtIdentifier.add(showStmtIdentifier59.getTree());

                            }
                            break;

                    }


                    // AST REWRITE
                    // elements: showStmtIdentifier
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    if ( backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 196:46: -> ^( TOK_SHOWTABLES ( showStmtIdentifier )? )
                    {
                        // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:196:49: ^( TOK_SHOWTABLES ( showStmtIdentifier )? )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot(adaptor.create(TOK_SHOWTABLES, "TOK_SHOWTABLES"), root_1);

                        // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:196:66: ( showStmtIdentifier )?
                        if ( stream_showStmtIdentifier.hasNext() ) {
                            adaptor.addChild(root_1, stream_showStmtIdentifier.next());

                        }
                        stream_showStmtIdentifier.reset();

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    }

                    }
                    break;
                case 2 :
                    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:197:7: KW_SHOW KW_PARTITIONS Identifier
                    {
                    KW_SHOW60=(Token)input.LT(1);
                    match(input,KW_SHOW,FOLLOW_KW_SHOW_in_showStatement1040); if (failed) return retval;
                    if ( backtracking==0 ) stream_KW_SHOW.add(KW_SHOW60);

                    KW_PARTITIONS61=(Token)input.LT(1);
                    match(input,KW_PARTITIONS,FOLLOW_KW_PARTITIONS_in_showStatement1042); if (failed) return retval;
                    if ( backtracking==0 ) stream_KW_PARTITIONS.add(KW_PARTITIONS61);

                    Identifier62=(Token)input.LT(1);
                    match(input,Identifier,FOLLOW_Identifier_in_showStatement1044); if (failed) return retval;
                    if ( backtracking==0 ) stream_Identifier.add(Identifier62);


                    // AST REWRITE
                    // elements: Identifier
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    if ( backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 197:40: -> ^( TOK_SHOWPARTITIONS Identifier )
                    {
                        // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:197:43: ^( TOK_SHOWPARTITIONS Identifier )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot(adaptor.create(TOK_SHOWPARTITIONS, "TOK_SHOWPARTITIONS"), root_1);

                        adaptor.addChild(root_1, stream_Identifier.next());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    }

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e) {
          reportError(e);
          throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end showStatement

    public static class createFunctionStatement_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start createFunctionStatement
    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:200:1: createFunctionStatement : KW_CREATE KW_TEMPORARY KW_FUNCTION Identifier KW_AS StringLiteral -> ^( TOK_CREATEFUNCTION Identifier StringLiteral ) ;
    public final createFunctionStatement_return createFunctionStatement() throws RecognitionException {
        createFunctionStatement_return retval = new createFunctionStatement_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token KW_CREATE63=null;
        Token KW_TEMPORARY64=null;
        Token KW_FUNCTION65=null;
        Token Identifier66=null;
        Token KW_AS67=null;
        Token StringLiteral68=null;

        CommonTree KW_CREATE63_tree=null;
        CommonTree KW_TEMPORARY64_tree=null;
        CommonTree KW_FUNCTION65_tree=null;
        CommonTree Identifier66_tree=null;
        CommonTree KW_AS67_tree=null;
        CommonTree StringLiteral68_tree=null;
        RewriteRuleTokenStream stream_StringLiteral=new RewriteRuleTokenStream(adaptor,"token StringLiteral");
        RewriteRuleTokenStream stream_KW_AS=new RewriteRuleTokenStream(adaptor,"token KW_AS");
        RewriteRuleTokenStream stream_KW_CREATE=new RewriteRuleTokenStream(adaptor,"token KW_CREATE");
        RewriteRuleTokenStream stream_KW_FUNCTION=new RewriteRuleTokenStream(adaptor,"token KW_FUNCTION");
        RewriteRuleTokenStream stream_KW_TEMPORARY=new RewriteRuleTokenStream(adaptor,"token KW_TEMPORARY");
        RewriteRuleTokenStream stream_Identifier=new RewriteRuleTokenStream(adaptor,"token Identifier");

        try {
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:201:5: ( KW_CREATE KW_TEMPORARY KW_FUNCTION Identifier KW_AS StringLiteral -> ^( TOK_CREATEFUNCTION Identifier StringLiteral ) )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:201:7: KW_CREATE KW_TEMPORARY KW_FUNCTION Identifier KW_AS StringLiteral
            {
            KW_CREATE63=(Token)input.LT(1);
            match(input,KW_CREATE,FOLLOW_KW_CREATE_in_createFunctionStatement1073); if (failed) return retval;
            if ( backtracking==0 ) stream_KW_CREATE.add(KW_CREATE63);

            KW_TEMPORARY64=(Token)input.LT(1);
            match(input,KW_TEMPORARY,FOLLOW_KW_TEMPORARY_in_createFunctionStatement1075); if (failed) return retval;
            if ( backtracking==0 ) stream_KW_TEMPORARY.add(KW_TEMPORARY64);

            KW_FUNCTION65=(Token)input.LT(1);
            match(input,KW_FUNCTION,FOLLOW_KW_FUNCTION_in_createFunctionStatement1077); if (failed) return retval;
            if ( backtracking==0 ) stream_KW_FUNCTION.add(KW_FUNCTION65);

            Identifier66=(Token)input.LT(1);
            match(input,Identifier,FOLLOW_Identifier_in_createFunctionStatement1079); if (failed) return retval;
            if ( backtracking==0 ) stream_Identifier.add(Identifier66);

            KW_AS67=(Token)input.LT(1);
            match(input,KW_AS,FOLLOW_KW_AS_in_createFunctionStatement1081); if (failed) return retval;
            if ( backtracking==0 ) stream_KW_AS.add(KW_AS67);

            StringLiteral68=(Token)input.LT(1);
            match(input,StringLiteral,FOLLOW_StringLiteral_in_createFunctionStatement1083); if (failed) return retval;
            if ( backtracking==0 ) stream_StringLiteral.add(StringLiteral68);


            // AST REWRITE
            // elements: Identifier, StringLiteral
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 202:5: -> ^( TOK_CREATEFUNCTION Identifier StringLiteral )
            {
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:202:8: ^( TOK_CREATEFUNCTION Identifier StringLiteral )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(adaptor.create(TOK_CREATEFUNCTION, "TOK_CREATEFUNCTION"), root_1);

                adaptor.addChild(root_1, stream_Identifier.next());
                adaptor.addChild(root_1, stream_StringLiteral.next());

                adaptor.addChild(root_0, root_1);
                }

            }

            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e) {
          reportError(e);
          throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end createFunctionStatement

    public static class showStmtIdentifier_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start showStmtIdentifier
    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:205:1: showStmtIdentifier : ( Identifier | StringLiteral );
    public final showStmtIdentifier_return showStmtIdentifier() throws RecognitionException {
        showStmtIdentifier_return retval = new showStmtIdentifier_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token set69=null;

        CommonTree set69_tree=null;

        try {
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:206:5: ( Identifier | StringLiteral )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:
            {
            root_0 = (CommonTree)adaptor.nil();

            set69=(Token)input.LT(1);
            if ( input.LA(1)==StringLiteral||input.LA(1)==Identifier ) {
                input.consume();
                if ( backtracking==0 ) adaptor.addChild(root_0, adaptor.create(set69));
                errorRecovery=false;failed=false;
            }
            else {
                if (backtracking>0) {failed=true; return retval;}
                MismatchedSetException mse =
                    new MismatchedSetException(null,input);
                recoverFromMismatchedSet(input,mse,FOLLOW_set_in_showStmtIdentifier0);    throw mse;
            }


            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e) {
          reportError(e);
          throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end showStmtIdentifier

    public static class tableComment_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start tableComment
    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:210:1: tableComment : KW_COMMENT comment= StringLiteral -> ^( TOK_TABLECOMMENT $comment) ;
    public final tableComment_return tableComment() throws RecognitionException {
        tableComment_return retval = new tableComment_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token comment=null;
        Token KW_COMMENT70=null;

        CommonTree comment_tree=null;
        CommonTree KW_COMMENT70_tree=null;
        RewriteRuleTokenStream stream_StringLiteral=new RewriteRuleTokenStream(adaptor,"token StringLiteral");
        RewriteRuleTokenStream stream_KW_COMMENT=new RewriteRuleTokenStream(adaptor,"token KW_COMMENT");

        try {
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:211:5: ( KW_COMMENT comment= StringLiteral -> ^( TOK_TABLECOMMENT $comment) )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:212:7: KW_COMMENT comment= StringLiteral
            {
            KW_COMMENT70=(Token)input.LT(1);
            match(input,KW_COMMENT,FOLLOW_KW_COMMENT_in_tableComment1145); if (failed) return retval;
            if ( backtracking==0 ) stream_KW_COMMENT.add(KW_COMMENT70);

            comment=(Token)input.LT(1);
            match(input,StringLiteral,FOLLOW_StringLiteral_in_tableComment1149); if (failed) return retval;
            if ( backtracking==0 ) stream_StringLiteral.add(comment);


            // AST REWRITE
            // elements: comment
            // token labels: comment
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleTokenStream stream_comment=new RewriteRuleTokenStream(adaptor,"token comment",comment);
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 212:41: -> ^( TOK_TABLECOMMENT $comment)
            {
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:212:44: ^( TOK_TABLECOMMENT $comment)
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(adaptor.create(TOK_TABLECOMMENT, "TOK_TABLECOMMENT"), root_1);

                adaptor.addChild(root_1, stream_comment.next());

                adaptor.addChild(root_0, root_1);
                }

            }

            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e) {
          reportError(e);
          throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end tableComment

    public static class tablePartition_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start tablePartition
    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:215:1: tablePartition : KW_PARTITIONED KW_BY LPAREN columnNameTypeList RPAREN -> ^( TOK_TABLEPARTCOLS columnNameTypeList ) ;
    public final tablePartition_return tablePartition() throws RecognitionException {
        tablePartition_return retval = new tablePartition_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token KW_PARTITIONED71=null;
        Token KW_BY72=null;
        Token LPAREN73=null;
        Token RPAREN75=null;
        columnNameTypeList_return columnNameTypeList74 = null;


        CommonTree KW_PARTITIONED71_tree=null;
        CommonTree KW_BY72_tree=null;
        CommonTree LPAREN73_tree=null;
        CommonTree RPAREN75_tree=null;
        RewriteRuleTokenStream stream_RPAREN=new RewriteRuleTokenStream(adaptor,"token RPAREN");
        RewriteRuleTokenStream stream_KW_PARTITIONED=new RewriteRuleTokenStream(adaptor,"token KW_PARTITIONED");
        RewriteRuleTokenStream stream_LPAREN=new RewriteRuleTokenStream(adaptor,"token LPAREN");
        RewriteRuleTokenStream stream_KW_BY=new RewriteRuleTokenStream(adaptor,"token KW_BY");
        RewriteRuleSubtreeStream stream_columnNameTypeList=new RewriteRuleSubtreeStream(adaptor,"rule columnNameTypeList");
        try {
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:216:5: ( KW_PARTITIONED KW_BY LPAREN columnNameTypeList RPAREN -> ^( TOK_TABLEPARTCOLS columnNameTypeList ) )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:216:7: KW_PARTITIONED KW_BY LPAREN columnNameTypeList RPAREN
            {
            KW_PARTITIONED71=(Token)input.LT(1);
            match(input,KW_PARTITIONED,FOLLOW_KW_PARTITIONED_in_tablePartition1176); if (failed) return retval;
            if ( backtracking==0 ) stream_KW_PARTITIONED.add(KW_PARTITIONED71);

            KW_BY72=(Token)input.LT(1);
            match(input,KW_BY,FOLLOW_KW_BY_in_tablePartition1178); if (failed) return retval;
            if ( backtracking==0 ) stream_KW_BY.add(KW_BY72);

            LPAREN73=(Token)input.LT(1);
            match(input,LPAREN,FOLLOW_LPAREN_in_tablePartition1180); if (failed) return retval;
            if ( backtracking==0 ) stream_LPAREN.add(LPAREN73);

            pushFollow(FOLLOW_columnNameTypeList_in_tablePartition1182);
            columnNameTypeList74=columnNameTypeList();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_columnNameTypeList.add(columnNameTypeList74.getTree());
            RPAREN75=(Token)input.LT(1);
            match(input,RPAREN,FOLLOW_RPAREN_in_tablePartition1184); if (failed) return retval;
            if ( backtracking==0 ) stream_RPAREN.add(RPAREN75);


            // AST REWRITE
            // elements: columnNameTypeList
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 217:5: -> ^( TOK_TABLEPARTCOLS columnNameTypeList )
            {
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:217:8: ^( TOK_TABLEPARTCOLS columnNameTypeList )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(adaptor.create(TOK_TABLEPARTCOLS, "TOK_TABLEPARTCOLS"), root_1);

                adaptor.addChild(root_1, stream_columnNameTypeList.next());

                adaptor.addChild(root_0, root_1);
                }

            }

            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e) {
          reportError(e);
          throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end tablePartition

    public static class tableBuckets_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start tableBuckets
    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:220:1: tableBuckets : KW_CLUSTERED KW_BY LPAREN bucketCols= columnNameList RPAREN ( KW_SORTED KW_BY LPAREN sortCols= columnNameOrderList RPAREN )? KW_INTO num= Number KW_BUCKETS -> ^( TOK_TABLEBUCKETS $bucketCols ( $sortCols)? $num) ;
    public final tableBuckets_return tableBuckets() throws RecognitionException {
        tableBuckets_return retval = new tableBuckets_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token num=null;
        Token KW_CLUSTERED76=null;
        Token KW_BY77=null;
        Token LPAREN78=null;
        Token RPAREN79=null;
        Token KW_SORTED80=null;
        Token KW_BY81=null;
        Token LPAREN82=null;
        Token RPAREN83=null;
        Token KW_INTO84=null;
        Token KW_BUCKETS85=null;
        columnNameList_return bucketCols = null;

        columnNameOrderList_return sortCols = null;


        CommonTree num_tree=null;
        CommonTree KW_CLUSTERED76_tree=null;
        CommonTree KW_BY77_tree=null;
        CommonTree LPAREN78_tree=null;
        CommonTree RPAREN79_tree=null;
        CommonTree KW_SORTED80_tree=null;
        CommonTree KW_BY81_tree=null;
        CommonTree LPAREN82_tree=null;
        CommonTree RPAREN83_tree=null;
        CommonTree KW_INTO84_tree=null;
        CommonTree KW_BUCKETS85_tree=null;
        RewriteRuleTokenStream stream_RPAREN=new RewriteRuleTokenStream(adaptor,"token RPAREN");
        RewriteRuleTokenStream stream_KW_INTO=new RewriteRuleTokenStream(adaptor,"token KW_INTO");
        RewriteRuleTokenStream stream_Number=new RewriteRuleTokenStream(adaptor,"token Number");
        RewriteRuleTokenStream stream_KW_BUCKETS=new RewriteRuleTokenStream(adaptor,"token KW_BUCKETS");
        RewriteRuleTokenStream stream_KW_CLUSTERED=new RewriteRuleTokenStream(adaptor,"token KW_CLUSTERED");
        RewriteRuleTokenStream stream_LPAREN=new RewriteRuleTokenStream(adaptor,"token LPAREN");
        RewriteRuleTokenStream stream_KW_BY=new RewriteRuleTokenStream(adaptor,"token KW_BY");
        RewriteRuleTokenStream stream_KW_SORTED=new RewriteRuleTokenStream(adaptor,"token KW_SORTED");
        RewriteRuleSubtreeStream stream_columnNameList=new RewriteRuleSubtreeStream(adaptor,"rule columnNameList");
        RewriteRuleSubtreeStream stream_columnNameOrderList=new RewriteRuleSubtreeStream(adaptor,"rule columnNameOrderList");
        try {
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:221:5: ( KW_CLUSTERED KW_BY LPAREN bucketCols= columnNameList RPAREN ( KW_SORTED KW_BY LPAREN sortCols= columnNameOrderList RPAREN )? KW_INTO num= Number KW_BUCKETS -> ^( TOK_TABLEBUCKETS $bucketCols ( $sortCols)? $num) )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:222:7: KW_CLUSTERED KW_BY LPAREN bucketCols= columnNameList RPAREN ( KW_SORTED KW_BY LPAREN sortCols= columnNameOrderList RPAREN )? KW_INTO num= Number KW_BUCKETS
            {
            KW_CLUSTERED76=(Token)input.LT(1);
            match(input,KW_CLUSTERED,FOLLOW_KW_CLUSTERED_in_tableBuckets1220); if (failed) return retval;
            if ( backtracking==0 ) stream_KW_CLUSTERED.add(KW_CLUSTERED76);

            KW_BY77=(Token)input.LT(1);
            match(input,KW_BY,FOLLOW_KW_BY_in_tableBuckets1222); if (failed) return retval;
            if ( backtracking==0 ) stream_KW_BY.add(KW_BY77);

            LPAREN78=(Token)input.LT(1);
            match(input,LPAREN,FOLLOW_LPAREN_in_tableBuckets1224); if (failed) return retval;
            if ( backtracking==0 ) stream_LPAREN.add(LPAREN78);

            pushFollow(FOLLOW_columnNameList_in_tableBuckets1228);
            bucketCols=columnNameList();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_columnNameList.add(bucketCols.getTree());
            RPAREN79=(Token)input.LT(1);
            match(input,RPAREN,FOLLOW_RPAREN_in_tableBuckets1230); if (failed) return retval;
            if ( backtracking==0 ) stream_RPAREN.add(RPAREN79);

            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:222:66: ( KW_SORTED KW_BY LPAREN sortCols= columnNameOrderList RPAREN )?
            int alt20=2;
            int LA20_0 = input.LA(1);

            if ( (LA20_0==KW_SORTED) ) {
                alt20=1;
            }
            switch (alt20) {
                case 1 :
                    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:222:67: KW_SORTED KW_BY LPAREN sortCols= columnNameOrderList RPAREN
                    {
                    KW_SORTED80=(Token)input.LT(1);
                    match(input,KW_SORTED,FOLLOW_KW_SORTED_in_tableBuckets1233); if (failed) return retval;
                    if ( backtracking==0 ) stream_KW_SORTED.add(KW_SORTED80);

                    KW_BY81=(Token)input.LT(1);
                    match(input,KW_BY,FOLLOW_KW_BY_in_tableBuckets1235); if (failed) return retval;
                    if ( backtracking==0 ) stream_KW_BY.add(KW_BY81);

                    LPAREN82=(Token)input.LT(1);
                    match(input,LPAREN,FOLLOW_LPAREN_in_tableBuckets1237); if (failed) return retval;
                    if ( backtracking==0 ) stream_LPAREN.add(LPAREN82);

                    pushFollow(FOLLOW_columnNameOrderList_in_tableBuckets1241);
                    sortCols=columnNameOrderList();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_columnNameOrderList.add(sortCols.getTree());
                    RPAREN83=(Token)input.LT(1);
                    match(input,RPAREN,FOLLOW_RPAREN_in_tableBuckets1243); if (failed) return retval;
                    if ( backtracking==0 ) stream_RPAREN.add(RPAREN83);


                    }
                    break;

            }

            KW_INTO84=(Token)input.LT(1);
            match(input,KW_INTO,FOLLOW_KW_INTO_in_tableBuckets1247); if (failed) return retval;
            if ( backtracking==0 ) stream_KW_INTO.add(KW_INTO84);

            num=(Token)input.LT(1);
            match(input,Number,FOLLOW_Number_in_tableBuckets1251); if (failed) return retval;
            if ( backtracking==0 ) stream_Number.add(num);

            KW_BUCKETS85=(Token)input.LT(1);
            match(input,KW_BUCKETS,FOLLOW_KW_BUCKETS_in_tableBuckets1253); if (failed) return retval;
            if ( backtracking==0 ) stream_KW_BUCKETS.add(KW_BUCKETS85);


            // AST REWRITE
            // elements: num, sortCols, bucketCols
            // token labels: num
            // rule labels: sortCols, retval, bucketCols
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleTokenStream stream_num=new RewriteRuleTokenStream(adaptor,"token num",num);
            RewriteRuleSubtreeStream stream_sortCols=new RewriteRuleSubtreeStream(adaptor,"token sortCols",sortCols!=null?sortCols.tree:null);
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);
            RewriteRuleSubtreeStream stream_bucketCols=new RewriteRuleSubtreeStream(adaptor,"token bucketCols",bucketCols!=null?bucketCols.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 223:5: -> ^( TOK_TABLEBUCKETS $bucketCols ( $sortCols)? $num)
            {
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:223:8: ^( TOK_TABLEBUCKETS $bucketCols ( $sortCols)? $num)
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(adaptor.create(TOK_TABLEBUCKETS, "TOK_TABLEBUCKETS"), root_1);

                adaptor.addChild(root_1, stream_bucketCols.next());
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:223:39: ( $sortCols)?
                if ( stream_sortCols.hasNext() ) {
                    adaptor.addChild(root_1, stream_sortCols.next());

                }
                stream_sortCols.reset();
                adaptor.addChild(root_1, stream_num.next());

                adaptor.addChild(root_0, root_1);
                }

            }

            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e) {
          reportError(e);
          throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end tableBuckets

    public static class tableRowFormat_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start tableRowFormat
    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:226:1: tableRowFormat : ( KW_ROW KW_FORMAT KW_DELIMITED ( tableRowFormatFieldIdentifier )? ( tableRowFormatCollItemsIdentifier )? ( tableRowFormatMapKeysIdentifier )? ( tableRowFormatLinesIdentifier )? -> ^( TOK_TABLEROWFORMAT ( tableRowFormatFieldIdentifier )? ( tableRowFormatCollItemsIdentifier )? ( tableRowFormatMapKeysIdentifier )? ( tableRowFormatLinesIdentifier )? ) | KW_ROW KW_FORMAT KW_SERIALIZER name= StringLiteral ( tableSerializerProperties )? -> ^( TOK_TABLESERIALIZER $name ( tableSerializerProperties )? ) );
    public final tableRowFormat_return tableRowFormat() throws RecognitionException {
        tableRowFormat_return retval = new tableRowFormat_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token name=null;
        Token KW_ROW86=null;
        Token KW_FORMAT87=null;
        Token KW_DELIMITED88=null;
        Token KW_ROW93=null;
        Token KW_FORMAT94=null;
        Token KW_SERIALIZER95=null;
        tableRowFormatFieldIdentifier_return tableRowFormatFieldIdentifier89 = null;

        tableRowFormatCollItemsIdentifier_return tableRowFormatCollItemsIdentifier90 = null;

        tableRowFormatMapKeysIdentifier_return tableRowFormatMapKeysIdentifier91 = null;

        tableRowFormatLinesIdentifier_return tableRowFormatLinesIdentifier92 = null;

        tableSerializerProperties_return tableSerializerProperties96 = null;


        CommonTree name_tree=null;
        CommonTree KW_ROW86_tree=null;
        CommonTree KW_FORMAT87_tree=null;
        CommonTree KW_DELIMITED88_tree=null;
        CommonTree KW_ROW93_tree=null;
        CommonTree KW_FORMAT94_tree=null;
        CommonTree KW_SERIALIZER95_tree=null;
        RewriteRuleTokenStream stream_StringLiteral=new RewriteRuleTokenStream(adaptor,"token StringLiteral");
        RewriteRuleTokenStream stream_KW_DELIMITED=new RewriteRuleTokenStream(adaptor,"token KW_DELIMITED");
        RewriteRuleTokenStream stream_KW_ROW=new RewriteRuleTokenStream(adaptor,"token KW_ROW");
        RewriteRuleTokenStream stream_KW_FORMAT=new RewriteRuleTokenStream(adaptor,"token KW_FORMAT");
        RewriteRuleTokenStream stream_KW_SERIALIZER=new RewriteRuleTokenStream(adaptor,"token KW_SERIALIZER");
        RewriteRuleSubtreeStream stream_tableRowFormatMapKeysIdentifier=new RewriteRuleSubtreeStream(adaptor,"rule tableRowFormatMapKeysIdentifier");
        RewriteRuleSubtreeStream stream_tableRowFormatFieldIdentifier=new RewriteRuleSubtreeStream(adaptor,"rule tableRowFormatFieldIdentifier");
        RewriteRuleSubtreeStream stream_tableRowFormatCollItemsIdentifier=new RewriteRuleSubtreeStream(adaptor,"rule tableRowFormatCollItemsIdentifier");
        RewriteRuleSubtreeStream stream_tableSerializerProperties=new RewriteRuleSubtreeStream(adaptor,"rule tableSerializerProperties");
        RewriteRuleSubtreeStream stream_tableRowFormatLinesIdentifier=new RewriteRuleSubtreeStream(adaptor,"rule tableRowFormatLinesIdentifier");
        try {
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:227:5: ( KW_ROW KW_FORMAT KW_DELIMITED ( tableRowFormatFieldIdentifier )? ( tableRowFormatCollItemsIdentifier )? ( tableRowFormatMapKeysIdentifier )? ( tableRowFormatLinesIdentifier )? -> ^( TOK_TABLEROWFORMAT ( tableRowFormatFieldIdentifier )? ( tableRowFormatCollItemsIdentifier )? ( tableRowFormatMapKeysIdentifier )? ( tableRowFormatLinesIdentifier )? ) | KW_ROW KW_FORMAT KW_SERIALIZER name= StringLiteral ( tableSerializerProperties )? -> ^( TOK_TABLESERIALIZER $name ( tableSerializerProperties )? ) )
            int alt26=2;
            int LA26_0 = input.LA(1);

            if ( (LA26_0==KW_ROW) ) {
                int LA26_1 = input.LA(2);

                if ( (LA26_1==KW_FORMAT) ) {
                    int LA26_2 = input.LA(3);

                    if ( (LA26_2==KW_DELIMITED) ) {
                        alt26=1;
                    }
                    else if ( (LA26_2==KW_SERIALIZER) ) {
                        alt26=2;
                    }
                    else {
                        if (backtracking>0) {failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("226:1: tableRowFormat : ( KW_ROW KW_FORMAT KW_DELIMITED ( tableRowFormatFieldIdentifier )? ( tableRowFormatCollItemsIdentifier )? ( tableRowFormatMapKeysIdentifier )? ( tableRowFormatLinesIdentifier )? -> ^( TOK_TABLEROWFORMAT ( tableRowFormatFieldIdentifier )? ( tableRowFormatCollItemsIdentifier )? ( tableRowFormatMapKeysIdentifier )? ( tableRowFormatLinesIdentifier )? ) | KW_ROW KW_FORMAT KW_SERIALIZER name= StringLiteral ( tableSerializerProperties )? -> ^( TOK_TABLESERIALIZER $name ( tableSerializerProperties )? ) );", 26, 2, input);

                        throw nvae;
                    }
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("226:1: tableRowFormat : ( KW_ROW KW_FORMAT KW_DELIMITED ( tableRowFormatFieldIdentifier )? ( tableRowFormatCollItemsIdentifier )? ( tableRowFormatMapKeysIdentifier )? ( tableRowFormatLinesIdentifier )? -> ^( TOK_TABLEROWFORMAT ( tableRowFormatFieldIdentifier )? ( tableRowFormatCollItemsIdentifier )? ( tableRowFormatMapKeysIdentifier )? ( tableRowFormatLinesIdentifier )? ) | KW_ROW KW_FORMAT KW_SERIALIZER name= StringLiteral ( tableSerializerProperties )? -> ^( TOK_TABLESERIALIZER $name ( tableSerializerProperties )? ) );", 26, 1, input);

                    throw nvae;
                }
            }
            else {
                if (backtracking>0) {failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("226:1: tableRowFormat : ( KW_ROW KW_FORMAT KW_DELIMITED ( tableRowFormatFieldIdentifier )? ( tableRowFormatCollItemsIdentifier )? ( tableRowFormatMapKeysIdentifier )? ( tableRowFormatLinesIdentifier )? -> ^( TOK_TABLEROWFORMAT ( tableRowFormatFieldIdentifier )? ( tableRowFormatCollItemsIdentifier )? ( tableRowFormatMapKeysIdentifier )? ( tableRowFormatLinesIdentifier )? ) | KW_ROW KW_FORMAT KW_SERIALIZER name= StringLiteral ( tableSerializerProperties )? -> ^( TOK_TABLESERIALIZER $name ( tableSerializerProperties )? ) );", 26, 0, input);

                throw nvae;
            }
            switch (alt26) {
                case 1 :
                    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:228:7: KW_ROW KW_FORMAT KW_DELIMITED ( tableRowFormatFieldIdentifier )? ( tableRowFormatCollItemsIdentifier )? ( tableRowFormatMapKeysIdentifier )? ( tableRowFormatLinesIdentifier )?
                    {
                    KW_ROW86=(Token)input.LT(1);
                    match(input,KW_ROW,FOLLOW_KW_ROW_in_tableRowFormat1297); if (failed) return retval;
                    if ( backtracking==0 ) stream_KW_ROW.add(KW_ROW86);

                    KW_FORMAT87=(Token)input.LT(1);
                    match(input,KW_FORMAT,FOLLOW_KW_FORMAT_in_tableRowFormat1299); if (failed) return retval;
                    if ( backtracking==0 ) stream_KW_FORMAT.add(KW_FORMAT87);

                    KW_DELIMITED88=(Token)input.LT(1);
                    match(input,KW_DELIMITED,FOLLOW_KW_DELIMITED_in_tableRowFormat1301); if (failed) return retval;
                    if ( backtracking==0 ) stream_KW_DELIMITED.add(KW_DELIMITED88);

                    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:228:37: ( tableRowFormatFieldIdentifier )?
                    int alt21=2;
                    int LA21_0 = input.LA(1);

                    if ( (LA21_0==KW_FIELDS) ) {
                        alt21=1;
                    }
                    switch (alt21) {
                        case 1 :
                            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:0:0: tableRowFormatFieldIdentifier
                            {
                            pushFollow(FOLLOW_tableRowFormatFieldIdentifier_in_tableRowFormat1303);
                            tableRowFormatFieldIdentifier89=tableRowFormatFieldIdentifier();
                            _fsp--;
                            if (failed) return retval;
                            if ( backtracking==0 ) stream_tableRowFormatFieldIdentifier.add(tableRowFormatFieldIdentifier89.getTree());

                            }
                            break;

                    }

                    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:228:68: ( tableRowFormatCollItemsIdentifier )?
                    int alt22=2;
                    int LA22_0 = input.LA(1);

                    if ( (LA22_0==KW_COLLECTION) ) {
                        alt22=1;
                    }
                    switch (alt22) {
                        case 1 :
                            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:0:0: tableRowFormatCollItemsIdentifier
                            {
                            pushFollow(FOLLOW_tableRowFormatCollItemsIdentifier_in_tableRowFormat1306);
                            tableRowFormatCollItemsIdentifier90=tableRowFormatCollItemsIdentifier();
                            _fsp--;
                            if (failed) return retval;
                            if ( backtracking==0 ) stream_tableRowFormatCollItemsIdentifier.add(tableRowFormatCollItemsIdentifier90.getTree());

                            }
                            break;

                    }

                    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:228:103: ( tableRowFormatMapKeysIdentifier )?
                    int alt23=2;
                    int LA23_0 = input.LA(1);

                    if ( (LA23_0==KW_MAP) ) {
                        alt23=1;
                    }
                    switch (alt23) {
                        case 1 :
                            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:0:0: tableRowFormatMapKeysIdentifier
                            {
                            pushFollow(FOLLOW_tableRowFormatMapKeysIdentifier_in_tableRowFormat1309);
                            tableRowFormatMapKeysIdentifier91=tableRowFormatMapKeysIdentifier();
                            _fsp--;
                            if (failed) return retval;
                            if ( backtracking==0 ) stream_tableRowFormatMapKeysIdentifier.add(tableRowFormatMapKeysIdentifier91.getTree());

                            }
                            break;

                    }

                    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:228:136: ( tableRowFormatLinesIdentifier )?
                    int alt24=2;
                    int LA24_0 = input.LA(1);

                    if ( (LA24_0==KW_LINES) ) {
                        alt24=1;
                    }
                    switch (alt24) {
                        case 1 :
                            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:0:0: tableRowFormatLinesIdentifier
                            {
                            pushFollow(FOLLOW_tableRowFormatLinesIdentifier_in_tableRowFormat1312);
                            tableRowFormatLinesIdentifier92=tableRowFormatLinesIdentifier();
                            _fsp--;
                            if (failed) return retval;
                            if ( backtracking==0 ) stream_tableRowFormatLinesIdentifier.add(tableRowFormatLinesIdentifier92.getTree());

                            }
                            break;

                    }


                    // AST REWRITE
                    // elements: tableRowFormatLinesIdentifier, tableRowFormatMapKeysIdentifier, tableRowFormatFieldIdentifier, tableRowFormatCollItemsIdentifier
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    if ( backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 229:5: -> ^( TOK_TABLEROWFORMAT ( tableRowFormatFieldIdentifier )? ( tableRowFormatCollItemsIdentifier )? ( tableRowFormatMapKeysIdentifier )? ( tableRowFormatLinesIdentifier )? )
                    {
                        // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:229:8: ^( TOK_TABLEROWFORMAT ( tableRowFormatFieldIdentifier )? ( tableRowFormatCollItemsIdentifier )? ( tableRowFormatMapKeysIdentifier )? ( tableRowFormatLinesIdentifier )? )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot(adaptor.create(TOK_TABLEROWFORMAT, "TOK_TABLEROWFORMAT"), root_1);

                        // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:229:29: ( tableRowFormatFieldIdentifier )?
                        if ( stream_tableRowFormatFieldIdentifier.hasNext() ) {
                            adaptor.addChild(root_1, stream_tableRowFormatFieldIdentifier.next());

                        }
                        stream_tableRowFormatFieldIdentifier.reset();
                        // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:229:60: ( tableRowFormatCollItemsIdentifier )?
                        if ( stream_tableRowFormatCollItemsIdentifier.hasNext() ) {
                            adaptor.addChild(root_1, stream_tableRowFormatCollItemsIdentifier.next());

                        }
                        stream_tableRowFormatCollItemsIdentifier.reset();
                        // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:229:95: ( tableRowFormatMapKeysIdentifier )?
                        if ( stream_tableRowFormatMapKeysIdentifier.hasNext() ) {
                            adaptor.addChild(root_1, stream_tableRowFormatMapKeysIdentifier.next());

                        }
                        stream_tableRowFormatMapKeysIdentifier.reset();
                        // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:229:128: ( tableRowFormatLinesIdentifier )?
                        if ( stream_tableRowFormatLinesIdentifier.hasNext() ) {
                            adaptor.addChild(root_1, stream_tableRowFormatLinesIdentifier.next());

                        }
                        stream_tableRowFormatLinesIdentifier.reset();

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    }

                    }
                    break;
                case 2 :
                    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:230:7: KW_ROW KW_FORMAT KW_SERIALIZER name= StringLiteral ( tableSerializerProperties )?
                    {
                    KW_ROW93=(Token)input.LT(1);
                    match(input,KW_ROW,FOLLOW_KW_ROW_in_tableRowFormat1344); if (failed) return retval;
                    if ( backtracking==0 ) stream_KW_ROW.add(KW_ROW93);

                    KW_FORMAT94=(Token)input.LT(1);
                    match(input,KW_FORMAT,FOLLOW_KW_FORMAT_in_tableRowFormat1346); if (failed) return retval;
                    if ( backtracking==0 ) stream_KW_FORMAT.add(KW_FORMAT94);

                    KW_SERIALIZER95=(Token)input.LT(1);
                    match(input,KW_SERIALIZER,FOLLOW_KW_SERIALIZER_in_tableRowFormat1348); if (failed) return retval;
                    if ( backtracking==0 ) stream_KW_SERIALIZER.add(KW_SERIALIZER95);

                    name=(Token)input.LT(1);
                    match(input,StringLiteral,FOLLOW_StringLiteral_in_tableRowFormat1352); if (failed) return retval;
                    if ( backtracking==0 ) stream_StringLiteral.add(name);

                    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:230:57: ( tableSerializerProperties )?
                    int alt25=2;
                    int LA25_0 = input.LA(1);

                    if ( (LA25_0==KW_WITH) ) {
                        alt25=1;
                    }
                    switch (alt25) {
                        case 1 :
                            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:0:0: tableSerializerProperties
                            {
                            pushFollow(FOLLOW_tableSerializerProperties_in_tableRowFormat1354);
                            tableSerializerProperties96=tableSerializerProperties();
                            _fsp--;
                            if (failed) return retval;
                            if ( backtracking==0 ) stream_tableSerializerProperties.add(tableSerializerProperties96.getTree());

                            }
                            break;

                    }


                    // AST REWRITE
                    // elements: name, tableSerializerProperties
                    // token labels: name
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    if ( backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleTokenStream stream_name=new RewriteRuleTokenStream(adaptor,"token name",name);
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 231:5: -> ^( TOK_TABLESERIALIZER $name ( tableSerializerProperties )? )
                    {
                        // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:231:8: ^( TOK_TABLESERIALIZER $name ( tableSerializerProperties )? )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot(adaptor.create(TOK_TABLESERIALIZER, "TOK_TABLESERIALIZER"), root_1);

                        adaptor.addChild(root_1, stream_name.next());
                        // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:231:36: ( tableSerializerProperties )?
                        if ( stream_tableSerializerProperties.hasNext() ) {
                            adaptor.addChild(root_1, stream_tableSerializerProperties.next());

                        }
                        stream_tableSerializerProperties.reset();

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    }

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e) {
          reportError(e);
          throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end tableRowFormat

    public static class tableSerializerProperties_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start tableSerializerProperties
    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:234:1: tableSerializerProperties : KW_WITH KW_PROPERTIES LPAREN propertiesList RPAREN -> ^( TOK_TABLSERDEPROPERTIES propertiesList ) ;
    public final tableSerializerProperties_return tableSerializerProperties() throws RecognitionException {
        tableSerializerProperties_return retval = new tableSerializerProperties_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token KW_WITH97=null;
        Token KW_PROPERTIES98=null;
        Token LPAREN99=null;
        Token RPAREN101=null;
        propertiesList_return propertiesList100 = null;


        CommonTree KW_WITH97_tree=null;
        CommonTree KW_PROPERTIES98_tree=null;
        CommonTree LPAREN99_tree=null;
        CommonTree RPAREN101_tree=null;
        RewriteRuleTokenStream stream_RPAREN=new RewriteRuleTokenStream(adaptor,"token RPAREN");
        RewriteRuleTokenStream stream_KW_WITH=new RewriteRuleTokenStream(adaptor,"token KW_WITH");
        RewriteRuleTokenStream stream_KW_PROPERTIES=new RewriteRuleTokenStream(adaptor,"token KW_PROPERTIES");
        RewriteRuleTokenStream stream_LPAREN=new RewriteRuleTokenStream(adaptor,"token LPAREN");
        RewriteRuleSubtreeStream stream_propertiesList=new RewriteRuleSubtreeStream(adaptor,"rule propertiesList");
        try {
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:235:5: ( KW_WITH KW_PROPERTIES LPAREN propertiesList RPAREN -> ^( TOK_TABLSERDEPROPERTIES propertiesList ) )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:236:7: KW_WITH KW_PROPERTIES LPAREN propertiesList RPAREN
            {
            KW_WITH97=(Token)input.LT(1);
            match(input,KW_WITH,FOLLOW_KW_WITH_in_tableSerializerProperties1394); if (failed) return retval;
            if ( backtracking==0 ) stream_KW_WITH.add(KW_WITH97);

            KW_PROPERTIES98=(Token)input.LT(1);
            match(input,KW_PROPERTIES,FOLLOW_KW_PROPERTIES_in_tableSerializerProperties1396); if (failed) return retval;
            if ( backtracking==0 ) stream_KW_PROPERTIES.add(KW_PROPERTIES98);

            LPAREN99=(Token)input.LT(1);
            match(input,LPAREN,FOLLOW_LPAREN_in_tableSerializerProperties1398); if (failed) return retval;
            if ( backtracking==0 ) stream_LPAREN.add(LPAREN99);

            pushFollow(FOLLOW_propertiesList_in_tableSerializerProperties1400);
            propertiesList100=propertiesList();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_propertiesList.add(propertiesList100.getTree());
            RPAREN101=(Token)input.LT(1);
            match(input,RPAREN,FOLLOW_RPAREN_in_tableSerializerProperties1402); if (failed) return retval;
            if ( backtracking==0 ) stream_RPAREN.add(RPAREN101);


            // AST REWRITE
            // elements: propertiesList
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 236:58: -> ^( TOK_TABLSERDEPROPERTIES propertiesList )
            {
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:236:61: ^( TOK_TABLSERDEPROPERTIES propertiesList )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(adaptor.create(TOK_TABLSERDEPROPERTIES, "TOK_TABLSERDEPROPERTIES"), root_1);

                adaptor.addChild(root_1, stream_propertiesList.next());

                adaptor.addChild(root_0, root_1);
                }

            }

            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e) {
          reportError(e);
          throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end tableSerializerProperties

    public static class propertiesList_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start propertiesList
    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:239:1: propertiesList : keyValueProperty ( COMMA keyValueProperty )* -> ^( TOK_TABLESERDEPROPLIST ( keyValueProperty )+ ) ;
    public final propertiesList_return propertiesList() throws RecognitionException {
        propertiesList_return retval = new propertiesList_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token COMMA103=null;
        keyValueProperty_return keyValueProperty102 = null;

        keyValueProperty_return keyValueProperty104 = null;


        CommonTree COMMA103_tree=null;
        RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
        RewriteRuleSubtreeStream stream_keyValueProperty=new RewriteRuleSubtreeStream(adaptor,"rule keyValueProperty");
        try {
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:240:5: ( keyValueProperty ( COMMA keyValueProperty )* -> ^( TOK_TABLESERDEPROPLIST ( keyValueProperty )+ ) )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:241:7: keyValueProperty ( COMMA keyValueProperty )*
            {
            pushFollow(FOLLOW_keyValueProperty_in_propertiesList1433);
            keyValueProperty102=keyValueProperty();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_keyValueProperty.add(keyValueProperty102.getTree());
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:241:24: ( COMMA keyValueProperty )*
            loop27:
            do {
                int alt27=2;
                int LA27_0 = input.LA(1);

                if ( (LA27_0==COMMA) ) {
                    alt27=1;
                }


                switch (alt27) {
            	case 1 :
            	    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:241:25: COMMA keyValueProperty
            	    {
            	    COMMA103=(Token)input.LT(1);
            	    match(input,COMMA,FOLLOW_COMMA_in_propertiesList1436); if (failed) return retval;
            	    if ( backtracking==0 ) stream_COMMA.add(COMMA103);

            	    pushFollow(FOLLOW_keyValueProperty_in_propertiesList1438);
            	    keyValueProperty104=keyValueProperty();
            	    _fsp--;
            	    if (failed) return retval;
            	    if ( backtracking==0 ) stream_keyValueProperty.add(keyValueProperty104.getTree());

            	    }
            	    break;

            	default :
            	    break loop27;
                }
            } while (true);


            // AST REWRITE
            // elements: keyValueProperty
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 241:50: -> ^( TOK_TABLESERDEPROPLIST ( keyValueProperty )+ )
            {
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:241:53: ^( TOK_TABLESERDEPROPLIST ( keyValueProperty )+ )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(adaptor.create(TOK_TABLESERDEPROPLIST, "TOK_TABLESERDEPROPLIST"), root_1);

                if ( !(stream_keyValueProperty.hasNext()) ) {
                    throw new RewriteEarlyExitException();
                }
                while ( stream_keyValueProperty.hasNext() ) {
                    adaptor.addChild(root_1, stream_keyValueProperty.next());

                }
                stream_keyValueProperty.reset();

                adaptor.addChild(root_0, root_1);
                }

            }

            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e) {
          reportError(e);
          throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end propertiesList

    public static class keyValueProperty_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start keyValueProperty
    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:244:1: keyValueProperty : key= StringLiteral EQUAL value= StringLiteral -> ^( TOKTABLESERDEPROPERTY $key $value) ;
    public final keyValueProperty_return keyValueProperty() throws RecognitionException {
        keyValueProperty_return retval = new keyValueProperty_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token key=null;
        Token value=null;
        Token EQUAL105=null;

        CommonTree key_tree=null;
        CommonTree value_tree=null;
        CommonTree EQUAL105_tree=null;
        RewriteRuleTokenStream stream_StringLiteral=new RewriteRuleTokenStream(adaptor,"token StringLiteral");
        RewriteRuleTokenStream stream_EQUAL=new RewriteRuleTokenStream(adaptor,"token EQUAL");

        try {
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:245:5: (key= StringLiteral EQUAL value= StringLiteral -> ^( TOKTABLESERDEPROPERTY $key $value) )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:246:7: key= StringLiteral EQUAL value= StringLiteral
            {
            key=(Token)input.LT(1);
            match(input,StringLiteral,FOLLOW_StringLiteral_in_keyValueProperty1474); if (failed) return retval;
            if ( backtracking==0 ) stream_StringLiteral.add(key);

            EQUAL105=(Token)input.LT(1);
            match(input,EQUAL,FOLLOW_EQUAL_in_keyValueProperty1476); if (failed) return retval;
            if ( backtracking==0 ) stream_EQUAL.add(EQUAL105);

            value=(Token)input.LT(1);
            match(input,StringLiteral,FOLLOW_StringLiteral_in_keyValueProperty1480); if (failed) return retval;
            if ( backtracking==0 ) stream_StringLiteral.add(value);


            // AST REWRITE
            // elements: key, value
            // token labels: value, key
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleTokenStream stream_value=new RewriteRuleTokenStream(adaptor,"token value",value);
            RewriteRuleTokenStream stream_key=new RewriteRuleTokenStream(adaptor,"token key",key);
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 246:51: -> ^( TOKTABLESERDEPROPERTY $key $value)
            {
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:246:54: ^( TOKTABLESERDEPROPERTY $key $value)
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(adaptor.create(TOKTABLESERDEPROPERTY, "TOKTABLESERDEPROPERTY"), root_1);

                adaptor.addChild(root_1, stream_key.next());
                adaptor.addChild(root_1, stream_value.next());

                adaptor.addChild(root_0, root_1);
                }

            }

            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e) {
          reportError(e);
          throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end keyValueProperty

    public static class tableRowFormatFieldIdentifier_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start tableRowFormatFieldIdentifier
    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:249:1: tableRowFormatFieldIdentifier : KW_FIELDS KW_TERMINATED KW_BY fldIdnt= StringLiteral -> ^( TOK_TABLEROWFORMATFIELD $fldIdnt) ;
    public final tableRowFormatFieldIdentifier_return tableRowFormatFieldIdentifier() throws RecognitionException {
        tableRowFormatFieldIdentifier_return retval = new tableRowFormatFieldIdentifier_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token fldIdnt=null;
        Token KW_FIELDS106=null;
        Token KW_TERMINATED107=null;
        Token KW_BY108=null;

        CommonTree fldIdnt_tree=null;
        CommonTree KW_FIELDS106_tree=null;
        CommonTree KW_TERMINATED107_tree=null;
        CommonTree KW_BY108_tree=null;
        RewriteRuleTokenStream stream_StringLiteral=new RewriteRuleTokenStream(adaptor,"token StringLiteral");
        RewriteRuleTokenStream stream_KW_FIELDS=new RewriteRuleTokenStream(adaptor,"token KW_FIELDS");
        RewriteRuleTokenStream stream_KW_TERMINATED=new RewriteRuleTokenStream(adaptor,"token KW_TERMINATED");
        RewriteRuleTokenStream stream_KW_BY=new RewriteRuleTokenStream(adaptor,"token KW_BY");

        try {
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:250:5: ( KW_FIELDS KW_TERMINATED KW_BY fldIdnt= StringLiteral -> ^( TOK_TABLEROWFORMATFIELD $fldIdnt) )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:251:7: KW_FIELDS KW_TERMINATED KW_BY fldIdnt= StringLiteral
            {
            KW_FIELDS106=(Token)input.LT(1);
            match(input,KW_FIELDS,FOLLOW_KW_FIELDS_in_tableRowFormatFieldIdentifier1515); if (failed) return retval;
            if ( backtracking==0 ) stream_KW_FIELDS.add(KW_FIELDS106);

            KW_TERMINATED107=(Token)input.LT(1);
            match(input,KW_TERMINATED,FOLLOW_KW_TERMINATED_in_tableRowFormatFieldIdentifier1517); if (failed) return retval;
            if ( backtracking==0 ) stream_KW_TERMINATED.add(KW_TERMINATED107);

            KW_BY108=(Token)input.LT(1);
            match(input,KW_BY,FOLLOW_KW_BY_in_tableRowFormatFieldIdentifier1519); if (failed) return retval;
            if ( backtracking==0 ) stream_KW_BY.add(KW_BY108);

            fldIdnt=(Token)input.LT(1);
            match(input,StringLiteral,FOLLOW_StringLiteral_in_tableRowFormatFieldIdentifier1523); if (failed) return retval;
            if ( backtracking==0 ) stream_StringLiteral.add(fldIdnt);


            // AST REWRITE
            // elements: fldIdnt
            // token labels: fldIdnt
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleTokenStream stream_fldIdnt=new RewriteRuleTokenStream(adaptor,"token fldIdnt",fldIdnt);
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 252:5: -> ^( TOK_TABLEROWFORMATFIELD $fldIdnt)
            {
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:252:8: ^( TOK_TABLEROWFORMATFIELD $fldIdnt)
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(adaptor.create(TOK_TABLEROWFORMATFIELD, "TOK_TABLEROWFORMATFIELD"), root_1);

                adaptor.addChild(root_1, stream_fldIdnt.next());

                adaptor.addChild(root_0, root_1);
                }

            }

            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e) {
          reportError(e);
          throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end tableRowFormatFieldIdentifier

    public static class tableRowFormatCollItemsIdentifier_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start tableRowFormatCollItemsIdentifier
    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:255:1: tableRowFormatCollItemsIdentifier : KW_COLLECTION KW_ITEMS KW_TERMINATED KW_BY collIdnt= StringLiteral -> ^( TOK_TABLEROWFORMATCOLLITEMS $collIdnt) ;
    public final tableRowFormatCollItemsIdentifier_return tableRowFormatCollItemsIdentifier() throws RecognitionException {
        tableRowFormatCollItemsIdentifier_return retval = new tableRowFormatCollItemsIdentifier_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token collIdnt=null;
        Token KW_COLLECTION109=null;
        Token KW_ITEMS110=null;
        Token KW_TERMINATED111=null;
        Token KW_BY112=null;

        CommonTree collIdnt_tree=null;
        CommonTree KW_COLLECTION109_tree=null;
        CommonTree KW_ITEMS110_tree=null;
        CommonTree KW_TERMINATED111_tree=null;
        CommonTree KW_BY112_tree=null;
        RewriteRuleTokenStream stream_StringLiteral=new RewriteRuleTokenStream(adaptor,"token StringLiteral");
        RewriteRuleTokenStream stream_KW_ITEMS=new RewriteRuleTokenStream(adaptor,"token KW_ITEMS");
        RewriteRuleTokenStream stream_KW_COLLECTION=new RewriteRuleTokenStream(adaptor,"token KW_COLLECTION");
        RewriteRuleTokenStream stream_KW_TERMINATED=new RewriteRuleTokenStream(adaptor,"token KW_TERMINATED");
        RewriteRuleTokenStream stream_KW_BY=new RewriteRuleTokenStream(adaptor,"token KW_BY");

        try {
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:256:5: ( KW_COLLECTION KW_ITEMS KW_TERMINATED KW_BY collIdnt= StringLiteral -> ^( TOK_TABLEROWFORMATCOLLITEMS $collIdnt) )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:257:7: KW_COLLECTION KW_ITEMS KW_TERMINATED KW_BY collIdnt= StringLiteral
            {
            KW_COLLECTION109=(Token)input.LT(1);
            match(input,KW_COLLECTION,FOLLOW_KW_COLLECTION_in_tableRowFormatCollItemsIdentifier1560); if (failed) return retval;
            if ( backtracking==0 ) stream_KW_COLLECTION.add(KW_COLLECTION109);

            KW_ITEMS110=(Token)input.LT(1);
            match(input,KW_ITEMS,FOLLOW_KW_ITEMS_in_tableRowFormatCollItemsIdentifier1562); if (failed) return retval;
            if ( backtracking==0 ) stream_KW_ITEMS.add(KW_ITEMS110);

            KW_TERMINATED111=(Token)input.LT(1);
            match(input,KW_TERMINATED,FOLLOW_KW_TERMINATED_in_tableRowFormatCollItemsIdentifier1564); if (failed) return retval;
            if ( backtracking==0 ) stream_KW_TERMINATED.add(KW_TERMINATED111);

            KW_BY112=(Token)input.LT(1);
            match(input,KW_BY,FOLLOW_KW_BY_in_tableRowFormatCollItemsIdentifier1566); if (failed) return retval;
            if ( backtracking==0 ) stream_KW_BY.add(KW_BY112);

            collIdnt=(Token)input.LT(1);
            match(input,StringLiteral,FOLLOW_StringLiteral_in_tableRowFormatCollItemsIdentifier1570); if (failed) return retval;
            if ( backtracking==0 ) stream_StringLiteral.add(collIdnt);


            // AST REWRITE
            // elements: collIdnt
            // token labels: collIdnt
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleTokenStream stream_collIdnt=new RewriteRuleTokenStream(adaptor,"token collIdnt",collIdnt);
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 258:5: -> ^( TOK_TABLEROWFORMATCOLLITEMS $collIdnt)
            {
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:258:8: ^( TOK_TABLEROWFORMATCOLLITEMS $collIdnt)
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(adaptor.create(TOK_TABLEROWFORMATCOLLITEMS, "TOK_TABLEROWFORMATCOLLITEMS"), root_1);

                adaptor.addChild(root_1, stream_collIdnt.next());

                adaptor.addChild(root_0, root_1);
                }

            }

            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e) {
          reportError(e);
          throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end tableRowFormatCollItemsIdentifier

    public static class tableRowFormatMapKeysIdentifier_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start tableRowFormatMapKeysIdentifier
    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:261:1: tableRowFormatMapKeysIdentifier : KW_MAP KW_KEYS KW_TERMINATED KW_BY mapKeysIdnt= StringLiteral -> ^( TOK_TABLEROWFORMATMAPKEYS $mapKeysIdnt) ;
    public final tableRowFormatMapKeysIdentifier_return tableRowFormatMapKeysIdentifier() throws RecognitionException {
        tableRowFormatMapKeysIdentifier_return retval = new tableRowFormatMapKeysIdentifier_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token mapKeysIdnt=null;
        Token KW_MAP113=null;
        Token KW_KEYS114=null;
        Token KW_TERMINATED115=null;
        Token KW_BY116=null;

        CommonTree mapKeysIdnt_tree=null;
        CommonTree KW_MAP113_tree=null;
        CommonTree KW_KEYS114_tree=null;
        CommonTree KW_TERMINATED115_tree=null;
        CommonTree KW_BY116_tree=null;
        RewriteRuleTokenStream stream_StringLiteral=new RewriteRuleTokenStream(adaptor,"token StringLiteral");
        RewriteRuleTokenStream stream_KW_KEYS=new RewriteRuleTokenStream(adaptor,"token KW_KEYS");
        RewriteRuleTokenStream stream_KW_MAP=new RewriteRuleTokenStream(adaptor,"token KW_MAP");
        RewriteRuleTokenStream stream_KW_TERMINATED=new RewriteRuleTokenStream(adaptor,"token KW_TERMINATED");
        RewriteRuleTokenStream stream_KW_BY=new RewriteRuleTokenStream(adaptor,"token KW_BY");

        try {
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:262:5: ( KW_MAP KW_KEYS KW_TERMINATED KW_BY mapKeysIdnt= StringLiteral -> ^( TOK_TABLEROWFORMATMAPKEYS $mapKeysIdnt) )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:263:7: KW_MAP KW_KEYS KW_TERMINATED KW_BY mapKeysIdnt= StringLiteral
            {
            KW_MAP113=(Token)input.LT(1);
            match(input,KW_MAP,FOLLOW_KW_MAP_in_tableRowFormatMapKeysIdentifier1606); if (failed) return retval;
            if ( backtracking==0 ) stream_KW_MAP.add(KW_MAP113);

            KW_KEYS114=(Token)input.LT(1);
            match(input,KW_KEYS,FOLLOW_KW_KEYS_in_tableRowFormatMapKeysIdentifier1608); if (failed) return retval;
            if ( backtracking==0 ) stream_KW_KEYS.add(KW_KEYS114);

            KW_TERMINATED115=(Token)input.LT(1);
            match(input,KW_TERMINATED,FOLLOW_KW_TERMINATED_in_tableRowFormatMapKeysIdentifier1610); if (failed) return retval;
            if ( backtracking==0 ) stream_KW_TERMINATED.add(KW_TERMINATED115);

            KW_BY116=(Token)input.LT(1);
            match(input,KW_BY,FOLLOW_KW_BY_in_tableRowFormatMapKeysIdentifier1612); if (failed) return retval;
            if ( backtracking==0 ) stream_KW_BY.add(KW_BY116);

            mapKeysIdnt=(Token)input.LT(1);
            match(input,StringLiteral,FOLLOW_StringLiteral_in_tableRowFormatMapKeysIdentifier1616); if (failed) return retval;
            if ( backtracking==0 ) stream_StringLiteral.add(mapKeysIdnt);


            // AST REWRITE
            // elements: mapKeysIdnt
            // token labels: mapKeysIdnt
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleTokenStream stream_mapKeysIdnt=new RewriteRuleTokenStream(adaptor,"token mapKeysIdnt",mapKeysIdnt);
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 264:5: -> ^( TOK_TABLEROWFORMATMAPKEYS $mapKeysIdnt)
            {
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:264:8: ^( TOK_TABLEROWFORMATMAPKEYS $mapKeysIdnt)
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(adaptor.create(TOK_TABLEROWFORMATMAPKEYS, "TOK_TABLEROWFORMATMAPKEYS"), root_1);

                adaptor.addChild(root_1, stream_mapKeysIdnt.next());

                adaptor.addChild(root_0, root_1);
                }

            }

            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e) {
          reportError(e);
          throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end tableRowFormatMapKeysIdentifier

    public static class tableRowFormatLinesIdentifier_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start tableRowFormatLinesIdentifier
    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:267:1: tableRowFormatLinesIdentifier : KW_LINES KW_TERMINATED KW_BY linesIdnt= StringLiteral -> ^( TOK_TABLEROWFORMATLINES $linesIdnt) ;
    public final tableRowFormatLinesIdentifier_return tableRowFormatLinesIdentifier() throws RecognitionException {
        tableRowFormatLinesIdentifier_return retval = new tableRowFormatLinesIdentifier_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token linesIdnt=null;
        Token KW_LINES117=null;
        Token KW_TERMINATED118=null;
        Token KW_BY119=null;

        CommonTree linesIdnt_tree=null;
        CommonTree KW_LINES117_tree=null;
        CommonTree KW_TERMINATED118_tree=null;
        CommonTree KW_BY119_tree=null;
        RewriteRuleTokenStream stream_StringLiteral=new RewriteRuleTokenStream(adaptor,"token StringLiteral");
        RewriteRuleTokenStream stream_KW_LINES=new RewriteRuleTokenStream(adaptor,"token KW_LINES");
        RewriteRuleTokenStream stream_KW_TERMINATED=new RewriteRuleTokenStream(adaptor,"token KW_TERMINATED");
        RewriteRuleTokenStream stream_KW_BY=new RewriteRuleTokenStream(adaptor,"token KW_BY");

        try {
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:268:5: ( KW_LINES KW_TERMINATED KW_BY linesIdnt= StringLiteral -> ^( TOK_TABLEROWFORMATLINES $linesIdnt) )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:269:7: KW_LINES KW_TERMINATED KW_BY linesIdnt= StringLiteral
            {
            KW_LINES117=(Token)input.LT(1);
            match(input,KW_LINES,FOLLOW_KW_LINES_in_tableRowFormatLinesIdentifier1652); if (failed) return retval;
            if ( backtracking==0 ) stream_KW_LINES.add(KW_LINES117);

            KW_TERMINATED118=(Token)input.LT(1);
            match(input,KW_TERMINATED,FOLLOW_KW_TERMINATED_in_tableRowFormatLinesIdentifier1654); if (failed) return retval;
            if ( backtracking==0 ) stream_KW_TERMINATED.add(KW_TERMINATED118);

            KW_BY119=(Token)input.LT(1);
            match(input,KW_BY,FOLLOW_KW_BY_in_tableRowFormatLinesIdentifier1656); if (failed) return retval;
            if ( backtracking==0 ) stream_KW_BY.add(KW_BY119);

            linesIdnt=(Token)input.LT(1);
            match(input,StringLiteral,FOLLOW_StringLiteral_in_tableRowFormatLinesIdentifier1660); if (failed) return retval;
            if ( backtracking==0 ) stream_StringLiteral.add(linesIdnt);


            // AST REWRITE
            // elements: linesIdnt
            // token labels: linesIdnt
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleTokenStream stream_linesIdnt=new RewriteRuleTokenStream(adaptor,"token linesIdnt",linesIdnt);
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 270:5: -> ^( TOK_TABLEROWFORMATLINES $linesIdnt)
            {
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:270:8: ^( TOK_TABLEROWFORMATLINES $linesIdnt)
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(adaptor.create(TOK_TABLEROWFORMATLINES, "TOK_TABLEROWFORMATLINES"), root_1);

                adaptor.addChild(root_1, stream_linesIdnt.next());

                adaptor.addChild(root_0, root_1);
                }

            }

            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e) {
          reportError(e);
          throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end tableRowFormatLinesIdentifier

    public static class tableFileFormat_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start tableFileFormat
    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:273:1: tableFileFormat : KW_STORED KW_AS KW_SEQUENCEFILE -> TOK_TBLSEQUENCEFILE ;
    public final tableFileFormat_return tableFileFormat() throws RecognitionException {
        tableFileFormat_return retval = new tableFileFormat_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token KW_STORED120=null;
        Token KW_AS121=null;
        Token KW_SEQUENCEFILE122=null;

        CommonTree KW_STORED120_tree=null;
        CommonTree KW_AS121_tree=null;
        CommonTree KW_SEQUENCEFILE122_tree=null;
        RewriteRuleTokenStream stream_KW_AS=new RewriteRuleTokenStream(adaptor,"token KW_AS");
        RewriteRuleTokenStream stream_KW_STORED=new RewriteRuleTokenStream(adaptor,"token KW_STORED");
        RewriteRuleTokenStream stream_KW_SEQUENCEFILE=new RewriteRuleTokenStream(adaptor,"token KW_SEQUENCEFILE");

        try {
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:274:5: ( KW_STORED KW_AS KW_SEQUENCEFILE -> TOK_TBLSEQUENCEFILE )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:275:7: KW_STORED KW_AS KW_SEQUENCEFILE
            {
            KW_STORED120=(Token)input.LT(1);
            match(input,KW_STORED,FOLLOW_KW_STORED_in_tableFileFormat1696); if (failed) return retval;
            if ( backtracking==0 ) stream_KW_STORED.add(KW_STORED120);

            KW_AS121=(Token)input.LT(1);
            match(input,KW_AS,FOLLOW_KW_AS_in_tableFileFormat1698); if (failed) return retval;
            if ( backtracking==0 ) stream_KW_AS.add(KW_AS121);

            KW_SEQUENCEFILE122=(Token)input.LT(1);
            match(input,KW_SEQUENCEFILE,FOLLOW_KW_SEQUENCEFILE_in_tableFileFormat1700); if (failed) return retval;
            if ( backtracking==0 ) stream_KW_SEQUENCEFILE.add(KW_SEQUENCEFILE122);


            // AST REWRITE
            // elements: 
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 275:40: -> TOK_TBLSEQUENCEFILE
            {
                adaptor.addChild(root_0, adaptor.create(TOK_TBLSEQUENCEFILE, "TOK_TBLSEQUENCEFILE"));

            }

            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e) {
          reportError(e);
          throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end tableFileFormat

    public static class tableLocation_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start tableLocation
    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:278:1: tableLocation : KW_LOCATION locn= StringLiteral -> ^( TOK_TABLELOCATION $locn) ;
    public final tableLocation_return tableLocation() throws RecognitionException {
        tableLocation_return retval = new tableLocation_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token locn=null;
        Token KW_LOCATION123=null;

        CommonTree locn_tree=null;
        CommonTree KW_LOCATION123_tree=null;
        RewriteRuleTokenStream stream_StringLiteral=new RewriteRuleTokenStream(adaptor,"token StringLiteral");
        RewriteRuleTokenStream stream_KW_LOCATION=new RewriteRuleTokenStream(adaptor,"token KW_LOCATION");

        try {
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:279:5: ( KW_LOCATION locn= StringLiteral -> ^( TOK_TABLELOCATION $locn) )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:280:7: KW_LOCATION locn= StringLiteral
            {
            KW_LOCATION123=(Token)input.LT(1);
            match(input,KW_LOCATION,FOLLOW_KW_LOCATION_in_tableLocation1728); if (failed) return retval;
            if ( backtracking==0 ) stream_KW_LOCATION.add(KW_LOCATION123);

            locn=(Token)input.LT(1);
            match(input,StringLiteral,FOLLOW_StringLiteral_in_tableLocation1732); if (failed) return retval;
            if ( backtracking==0 ) stream_StringLiteral.add(locn);


            // AST REWRITE
            // elements: locn
            // token labels: locn
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleTokenStream stream_locn=new RewriteRuleTokenStream(adaptor,"token locn",locn);
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 280:38: -> ^( TOK_TABLELOCATION $locn)
            {
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:280:41: ^( TOK_TABLELOCATION $locn)
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(adaptor.create(TOK_TABLELOCATION, "TOK_TABLELOCATION"), root_1);

                adaptor.addChild(root_1, stream_locn.next());

                adaptor.addChild(root_0, root_1);
                }

            }

            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e) {
          reportError(e);
          throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end tableLocation

    public static class columnNameTypeList_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start columnNameTypeList
    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:283:1: columnNameTypeList : columnNameType ( COMMA columnNameType )* -> ^( TOK_TABCOLLIST ( columnNameType )+ ) ;
    public final columnNameTypeList_return columnNameTypeList() throws RecognitionException {
        columnNameTypeList_return retval = new columnNameTypeList_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token COMMA125=null;
        columnNameType_return columnNameType124 = null;

        columnNameType_return columnNameType126 = null;


        CommonTree COMMA125_tree=null;
        RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
        RewriteRuleSubtreeStream stream_columnNameType=new RewriteRuleSubtreeStream(adaptor,"rule columnNameType");
        try {
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:284:5: ( columnNameType ( COMMA columnNameType )* -> ^( TOK_TABCOLLIST ( columnNameType )+ ) )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:284:7: columnNameType ( COMMA columnNameType )*
            {
            pushFollow(FOLLOW_columnNameType_in_columnNameTypeList1760);
            columnNameType124=columnNameType();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_columnNameType.add(columnNameType124.getTree());
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:284:22: ( COMMA columnNameType )*
            loop28:
            do {
                int alt28=2;
                int LA28_0 = input.LA(1);

                if ( (LA28_0==COMMA) ) {
                    alt28=1;
                }


                switch (alt28) {
            	case 1 :
            	    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:284:23: COMMA columnNameType
            	    {
            	    COMMA125=(Token)input.LT(1);
            	    match(input,COMMA,FOLLOW_COMMA_in_columnNameTypeList1763); if (failed) return retval;
            	    if ( backtracking==0 ) stream_COMMA.add(COMMA125);

            	    pushFollow(FOLLOW_columnNameType_in_columnNameTypeList1765);
            	    columnNameType126=columnNameType();
            	    _fsp--;
            	    if (failed) return retval;
            	    if ( backtracking==0 ) stream_columnNameType.add(columnNameType126.getTree());

            	    }
            	    break;

            	default :
            	    break loop28;
                }
            } while (true);


            // AST REWRITE
            // elements: columnNameType
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 284:46: -> ^( TOK_TABCOLLIST ( columnNameType )+ )
            {
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:284:49: ^( TOK_TABCOLLIST ( columnNameType )+ )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(adaptor.create(TOK_TABCOLLIST, "TOK_TABCOLLIST"), root_1);

                if ( !(stream_columnNameType.hasNext()) ) {
                    throw new RewriteEarlyExitException();
                }
                while ( stream_columnNameType.hasNext() ) {
                    adaptor.addChild(root_1, stream_columnNameType.next());

                }
                stream_columnNameType.reset();

                adaptor.addChild(root_0, root_1);
                }

            }

            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e) {
          reportError(e);
          throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end columnNameTypeList

    public static class columnNameList_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start columnNameList
    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:287:1: columnNameList : columnName ( COMMA columnName )* -> ^( TOK_TABCOLNAME ( columnName )+ ) ;
    public final columnNameList_return columnNameList() throws RecognitionException {
        columnNameList_return retval = new columnNameList_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token COMMA128=null;
        columnName_return columnName127 = null;

        columnName_return columnName129 = null;


        CommonTree COMMA128_tree=null;
        RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
        RewriteRuleSubtreeStream stream_columnName=new RewriteRuleSubtreeStream(adaptor,"rule columnName");
        try {
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:288:5: ( columnName ( COMMA columnName )* -> ^( TOK_TABCOLNAME ( columnName )+ ) )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:288:7: columnName ( COMMA columnName )*
            {
            pushFollow(FOLLOW_columnName_in_columnNameList1793);
            columnName127=columnName();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_columnName.add(columnName127.getTree());
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:288:18: ( COMMA columnName )*
            loop29:
            do {
                int alt29=2;
                int LA29_0 = input.LA(1);

                if ( (LA29_0==COMMA) ) {
                    alt29=1;
                }


                switch (alt29) {
            	case 1 :
            	    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:288:19: COMMA columnName
            	    {
            	    COMMA128=(Token)input.LT(1);
            	    match(input,COMMA,FOLLOW_COMMA_in_columnNameList1796); if (failed) return retval;
            	    if ( backtracking==0 ) stream_COMMA.add(COMMA128);

            	    pushFollow(FOLLOW_columnName_in_columnNameList1798);
            	    columnName129=columnName();
            	    _fsp--;
            	    if (failed) return retval;
            	    if ( backtracking==0 ) stream_columnName.add(columnName129.getTree());

            	    }
            	    break;

            	default :
            	    break loop29;
                }
            } while (true);


            // AST REWRITE
            // elements: columnName
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 288:38: -> ^( TOK_TABCOLNAME ( columnName )+ )
            {
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:288:41: ^( TOK_TABCOLNAME ( columnName )+ )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(adaptor.create(TOK_TABCOLNAME, "TOK_TABCOLNAME"), root_1);

                if ( !(stream_columnName.hasNext()) ) {
                    throw new RewriteEarlyExitException();
                }
                while ( stream_columnName.hasNext() ) {
                    adaptor.addChild(root_1, stream_columnName.next());

                }
                stream_columnName.reset();

                adaptor.addChild(root_0, root_1);
                }

            }

            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e) {
          reportError(e);
          throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end columnNameList

    public static class columnName_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start columnName
    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:291:1: columnName : Identifier ;
    public final columnName_return columnName() throws RecognitionException {
        columnName_return retval = new columnName_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token Identifier130=null;

        CommonTree Identifier130_tree=null;

        try {
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:292:5: ( Identifier )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:293:7: Identifier
            {
            root_0 = (CommonTree)adaptor.nil();

            Identifier130=(Token)input.LT(1);
            match(input,Identifier,FOLLOW_Identifier_in_columnName1832); if (failed) return retval;
            if ( backtracking==0 ) {
            Identifier130_tree = (CommonTree)adaptor.create(Identifier130);
            adaptor.addChild(root_0, Identifier130_tree);
            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e) {
          reportError(e);
          throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end columnName

    public static class columnNameOrderList_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start columnNameOrderList
    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:296:1: columnNameOrderList : columnNameOrder ( COMMA columnNameOrder )* -> ^( TOK_TABCOLNAME ( columnNameOrder )+ ) ;
    public final columnNameOrderList_return columnNameOrderList() throws RecognitionException {
        columnNameOrderList_return retval = new columnNameOrderList_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token COMMA132=null;
        columnNameOrder_return columnNameOrder131 = null;

        columnNameOrder_return columnNameOrder133 = null;


        CommonTree COMMA132_tree=null;
        RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
        RewriteRuleSubtreeStream stream_columnNameOrder=new RewriteRuleSubtreeStream(adaptor,"rule columnNameOrder");
        try {
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:297:5: ( columnNameOrder ( COMMA columnNameOrder )* -> ^( TOK_TABCOLNAME ( columnNameOrder )+ ) )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:297:7: columnNameOrder ( COMMA columnNameOrder )*
            {
            pushFollow(FOLLOW_columnNameOrder_in_columnNameOrderList1849);
            columnNameOrder131=columnNameOrder();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_columnNameOrder.add(columnNameOrder131.getTree());
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:297:23: ( COMMA columnNameOrder )*
            loop30:
            do {
                int alt30=2;
                int LA30_0 = input.LA(1);

                if ( (LA30_0==COMMA) ) {
                    alt30=1;
                }


                switch (alt30) {
            	case 1 :
            	    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:297:24: COMMA columnNameOrder
            	    {
            	    COMMA132=(Token)input.LT(1);
            	    match(input,COMMA,FOLLOW_COMMA_in_columnNameOrderList1852); if (failed) return retval;
            	    if ( backtracking==0 ) stream_COMMA.add(COMMA132);

            	    pushFollow(FOLLOW_columnNameOrder_in_columnNameOrderList1854);
            	    columnNameOrder133=columnNameOrder();
            	    _fsp--;
            	    if (failed) return retval;
            	    if ( backtracking==0 ) stream_columnNameOrder.add(columnNameOrder133.getTree());

            	    }
            	    break;

            	default :
            	    break loop30;
                }
            } while (true);


            // AST REWRITE
            // elements: columnNameOrder
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 297:48: -> ^( TOK_TABCOLNAME ( columnNameOrder )+ )
            {
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:297:51: ^( TOK_TABCOLNAME ( columnNameOrder )+ )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(adaptor.create(TOK_TABCOLNAME, "TOK_TABCOLNAME"), root_1);

                if ( !(stream_columnNameOrder.hasNext()) ) {
                    throw new RewriteEarlyExitException();
                }
                while ( stream_columnNameOrder.hasNext() ) {
                    adaptor.addChild(root_1, stream_columnNameOrder.next());

                }
                stream_columnNameOrder.reset();

                adaptor.addChild(root_0, root_1);
                }

            }

            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e) {
          reportError(e);
          throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end columnNameOrderList

    public static class columnNameOrder_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start columnNameOrder
    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:300:1: columnNameOrder : Identifier (asc= KW_ASC | desc= KW_DESC )? -> {$desc == null}? ^( TOK_TABSORTCOLNAMEASC Identifier ) -> ^( TOK_TABSORTCOLNAMEDESC Identifier ) ;
    public final columnNameOrder_return columnNameOrder() throws RecognitionException {
        columnNameOrder_return retval = new columnNameOrder_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token asc=null;
        Token desc=null;
        Token Identifier134=null;

        CommonTree asc_tree=null;
        CommonTree desc_tree=null;
        CommonTree Identifier134_tree=null;
        RewriteRuleTokenStream stream_KW_DESC=new RewriteRuleTokenStream(adaptor,"token KW_DESC");
        RewriteRuleTokenStream stream_Identifier=new RewriteRuleTokenStream(adaptor,"token Identifier");
        RewriteRuleTokenStream stream_KW_ASC=new RewriteRuleTokenStream(adaptor,"token KW_ASC");

        try {
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:301:5: ( Identifier (asc= KW_ASC | desc= KW_DESC )? -> {$desc == null}? ^( TOK_TABSORTCOLNAMEASC Identifier ) -> ^( TOK_TABSORTCOLNAMEDESC Identifier ) )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:301:7: Identifier (asc= KW_ASC | desc= KW_DESC )?
            {
            Identifier134=(Token)input.LT(1);
            match(input,Identifier,FOLLOW_Identifier_in_columnNameOrder1882); if (failed) return retval;
            if ( backtracking==0 ) stream_Identifier.add(Identifier134);

            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:301:18: (asc= KW_ASC | desc= KW_DESC )?
            int alt31=3;
            int LA31_0 = input.LA(1);

            if ( (LA31_0==KW_ASC) ) {
                alt31=1;
            }
            else if ( (LA31_0==KW_DESC) ) {
                alt31=2;
            }
            switch (alt31) {
                case 1 :
                    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:301:19: asc= KW_ASC
                    {
                    asc=(Token)input.LT(1);
                    match(input,KW_ASC,FOLLOW_KW_ASC_in_columnNameOrder1887); if (failed) return retval;
                    if ( backtracking==0 ) stream_KW_ASC.add(asc);


                    }
                    break;
                case 2 :
                    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:301:32: desc= KW_DESC
                    {
                    desc=(Token)input.LT(1);
                    match(input,KW_DESC,FOLLOW_KW_DESC_in_columnNameOrder1893); if (failed) return retval;
                    if ( backtracking==0 ) stream_KW_DESC.add(desc);


                    }
                    break;

            }


            // AST REWRITE
            // elements: Identifier, Identifier
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 302:5: -> {$desc == null}? ^( TOK_TABSORTCOLNAMEASC Identifier )
            if (desc == null) {
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:302:25: ^( TOK_TABSORTCOLNAMEASC Identifier )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(adaptor.create(TOK_TABSORTCOLNAMEASC, "TOK_TABSORTCOLNAMEASC"), root_1);

                adaptor.addChild(root_1, stream_Identifier.next());

                adaptor.addChild(root_0, root_1);
                }

            }
            else // 303:5: -> ^( TOK_TABSORTCOLNAMEDESC Identifier )
            {
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:303:25: ^( TOK_TABSORTCOLNAMEDESC Identifier )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(adaptor.create(TOK_TABSORTCOLNAMEDESC, "TOK_TABSORTCOLNAMEDESC"), root_1);

                adaptor.addChild(root_1, stream_Identifier.next());

                adaptor.addChild(root_0, root_1);
                }

            }

            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e) {
          reportError(e);
          throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end columnNameOrder

    public static class columnNameType_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start columnNameType
    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:306:1: columnNameType : colName= Identifier colType ( KW_COMMENT comment= StringLiteral )? -> {$comment == null}? ^( TOK_TABCOL $colName colType ) -> ^( TOK_TABCOL $colName colType $comment) ;
    public final columnNameType_return columnNameType() throws RecognitionException {
        columnNameType_return retval = new columnNameType_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token colName=null;
        Token comment=null;
        Token KW_COMMENT136=null;
        colType_return colType135 = null;


        CommonTree colName_tree=null;
        CommonTree comment_tree=null;
        CommonTree KW_COMMENT136_tree=null;
        RewriteRuleTokenStream stream_StringLiteral=new RewriteRuleTokenStream(adaptor,"token StringLiteral");
        RewriteRuleTokenStream stream_KW_COMMENT=new RewriteRuleTokenStream(adaptor,"token KW_COMMENT");
        RewriteRuleTokenStream stream_Identifier=new RewriteRuleTokenStream(adaptor,"token Identifier");
        RewriteRuleSubtreeStream stream_colType=new RewriteRuleSubtreeStream(adaptor,"rule colType");
        try {
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:307:5: (colName= Identifier colType ( KW_COMMENT comment= StringLiteral )? -> {$comment == null}? ^( TOK_TABCOL $colName colType ) -> ^( TOK_TABCOL $colName colType $comment) )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:307:7: colName= Identifier colType ( KW_COMMENT comment= StringLiteral )?
            {
            colName=(Token)input.LT(1);
            match(input,Identifier,FOLLOW_Identifier_in_columnNameType1958); if (failed) return retval;
            if ( backtracking==0 ) stream_Identifier.add(colName);

            pushFollow(FOLLOW_colType_in_columnNameType1960);
            colType135=colType();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_colType.add(colType135.getTree());
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:307:34: ( KW_COMMENT comment= StringLiteral )?
            int alt32=2;
            int LA32_0 = input.LA(1);

            if ( (LA32_0==KW_COMMENT) ) {
                alt32=1;
            }
            switch (alt32) {
                case 1 :
                    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:307:35: KW_COMMENT comment= StringLiteral
                    {
                    KW_COMMENT136=(Token)input.LT(1);
                    match(input,KW_COMMENT,FOLLOW_KW_COMMENT_in_columnNameType1963); if (failed) return retval;
                    if ( backtracking==0 ) stream_KW_COMMENT.add(KW_COMMENT136);

                    comment=(Token)input.LT(1);
                    match(input,StringLiteral,FOLLOW_StringLiteral_in_columnNameType1967); if (failed) return retval;
                    if ( backtracking==0 ) stream_StringLiteral.add(comment);


                    }
                    break;

            }


            // AST REWRITE
            // elements: colName, colName, colType, comment, colType
            // token labels: comment, colName
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleTokenStream stream_comment=new RewriteRuleTokenStream(adaptor,"token comment",comment);
            RewriteRuleTokenStream stream_colName=new RewriteRuleTokenStream(adaptor,"token colName",colName);
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 308:5: -> {$comment == null}? ^( TOK_TABCOL $colName colType )
            if (comment == null) {
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:308:28: ^( TOK_TABCOL $colName colType )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(adaptor.create(TOK_TABCOL, "TOK_TABCOL"), root_1);

                adaptor.addChild(root_1, stream_colName.next());
                adaptor.addChild(root_1, stream_colType.next());

                adaptor.addChild(root_0, root_1);
                }

            }
            else // 309:5: -> ^( TOK_TABCOL $colName colType $comment)
            {
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:309:28: ^( TOK_TABCOL $colName colType $comment)
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(adaptor.create(TOK_TABCOL, "TOK_TABCOL"), root_1);

                adaptor.addChild(root_1, stream_colName.next());
                adaptor.addChild(root_1, stream_colType.next());
                adaptor.addChild(root_1, stream_comment.next());

                adaptor.addChild(root_0, root_1);
                }

            }

            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e) {
          reportError(e);
          throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end columnNameType

    public static class colType_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start colType
    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:312:1: colType : ( primitiveType | listType | mapType );
    public final colType_return colType() throws RecognitionException {
        colType_return retval = new colType_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        primitiveType_return primitiveType137 = null;

        listType_return listType138 = null;

        mapType_return mapType139 = null;



        try {
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:313:5: ( primitiveType | listType | mapType )
            int alt33=3;
            switch ( input.LA(1) ) {
            case KW_TINYINT:
            case KW_INT:
            case KW_BIGINT:
            case KW_BOOLEAN:
            case KW_FLOAT:
            case KW_DOUBLE:
            case KW_DATE:
            case KW_DATETIME:
            case KW_TIMESTAMP:
            case KW_STRING:
                {
                alt33=1;
                }
                break;
            case KW_ARRAY:
                {
                alt33=2;
                }
                break;
            case KW_MAP:
                {
                alt33=3;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("312:1: colType : ( primitiveType | listType | mapType );", 33, 0, input);

                throw nvae;
            }

            switch (alt33) {
                case 1 :
                    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:313:7: primitiveType
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_primitiveType_in_colType2045);
                    primitiveType137=primitiveType();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, primitiveType137.getTree());

                    }
                    break;
                case 2 :
                    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:314:7: listType
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_listType_in_colType2053);
                    listType138=listType();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, listType138.getTree());

                    }
                    break;
                case 3 :
                    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:315:7: mapType
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_mapType_in_colType2061);
                    mapType139=mapType();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, mapType139.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e) {
          reportError(e);
          throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end colType

    public static class primitiveType_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start primitiveType
    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:318:1: primitiveType : ( KW_TINYINT -> TOK_TINYINT | KW_INT -> TOK_INT | KW_BIGINT -> TOK_BIGINT | KW_BOOLEAN -> TOK_BOOLEAN | KW_FLOAT -> TOK_FLOAT | KW_DOUBLE -> TOK_DOUBLE | KW_DATE -> TOK_DATE | KW_DATETIME -> TOK_DATETIME | KW_TIMESTAMP -> TOK_TIMESTAMP | KW_STRING -> TOK_STRING );
    public final primitiveType_return primitiveType() throws RecognitionException {
        primitiveType_return retval = new primitiveType_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token KW_TINYINT140=null;
        Token KW_INT141=null;
        Token KW_BIGINT142=null;
        Token KW_BOOLEAN143=null;
        Token KW_FLOAT144=null;
        Token KW_DOUBLE145=null;
        Token KW_DATE146=null;
        Token KW_DATETIME147=null;
        Token KW_TIMESTAMP148=null;
        Token KW_STRING149=null;

        CommonTree KW_TINYINT140_tree=null;
        CommonTree KW_INT141_tree=null;
        CommonTree KW_BIGINT142_tree=null;
        CommonTree KW_BOOLEAN143_tree=null;
        CommonTree KW_FLOAT144_tree=null;
        CommonTree KW_DOUBLE145_tree=null;
        CommonTree KW_DATE146_tree=null;
        CommonTree KW_DATETIME147_tree=null;
        CommonTree KW_TIMESTAMP148_tree=null;
        CommonTree KW_STRING149_tree=null;
        RewriteRuleTokenStream stream_KW_DATETIME=new RewriteRuleTokenStream(adaptor,"token KW_DATETIME");
        RewriteRuleTokenStream stream_KW_STRING=new RewriteRuleTokenStream(adaptor,"token KW_STRING");
        RewriteRuleTokenStream stream_KW_TIMESTAMP=new RewriteRuleTokenStream(adaptor,"token KW_TIMESTAMP");
        RewriteRuleTokenStream stream_KW_DATE=new RewriteRuleTokenStream(adaptor,"token KW_DATE");
        RewriteRuleTokenStream stream_KW_FLOAT=new RewriteRuleTokenStream(adaptor,"token KW_FLOAT");
        RewriteRuleTokenStream stream_KW_INT=new RewriteRuleTokenStream(adaptor,"token KW_INT");
        RewriteRuleTokenStream stream_KW_DOUBLE=new RewriteRuleTokenStream(adaptor,"token KW_DOUBLE");
        RewriteRuleTokenStream stream_KW_BIGINT=new RewriteRuleTokenStream(adaptor,"token KW_BIGINT");
        RewriteRuleTokenStream stream_KW_TINYINT=new RewriteRuleTokenStream(adaptor,"token KW_TINYINT");
        RewriteRuleTokenStream stream_KW_BOOLEAN=new RewriteRuleTokenStream(adaptor,"token KW_BOOLEAN");

        try {
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:319:5: ( KW_TINYINT -> TOK_TINYINT | KW_INT -> TOK_INT | KW_BIGINT -> TOK_BIGINT | KW_BOOLEAN -> TOK_BOOLEAN | KW_FLOAT -> TOK_FLOAT | KW_DOUBLE -> TOK_DOUBLE | KW_DATE -> TOK_DATE | KW_DATETIME -> TOK_DATETIME | KW_TIMESTAMP -> TOK_TIMESTAMP | KW_STRING -> TOK_STRING )
            int alt34=10;
            switch ( input.LA(1) ) {
            case KW_TINYINT:
                {
                alt34=1;
                }
                break;
            case KW_INT:
                {
                alt34=2;
                }
                break;
            case KW_BIGINT:
                {
                alt34=3;
                }
                break;
            case KW_BOOLEAN:
                {
                alt34=4;
                }
                break;
            case KW_FLOAT:
                {
                alt34=5;
                }
                break;
            case KW_DOUBLE:
                {
                alt34=6;
                }
                break;
            case KW_DATE:
                {
                alt34=7;
                }
                break;
            case KW_DATETIME:
                {
                alt34=8;
                }
                break;
            case KW_TIMESTAMP:
                {
                alt34=9;
                }
                break;
            case KW_STRING:
                {
                alt34=10;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("318:1: primitiveType : ( KW_TINYINT -> TOK_TINYINT | KW_INT -> TOK_INT | KW_BIGINT -> TOK_BIGINT | KW_BOOLEAN -> TOK_BOOLEAN | KW_FLOAT -> TOK_FLOAT | KW_DOUBLE -> TOK_DOUBLE | KW_DATE -> TOK_DATE | KW_DATETIME -> TOK_DATETIME | KW_TIMESTAMP -> TOK_TIMESTAMP | KW_STRING -> TOK_STRING );", 34, 0, input);

                throw nvae;
            }

            switch (alt34) {
                case 1 :
                    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:319:7: KW_TINYINT
                    {
                    KW_TINYINT140=(Token)input.LT(1);
                    match(input,KW_TINYINT,FOLLOW_KW_TINYINT_in_primitiveType2078); if (failed) return retval;
                    if ( backtracking==0 ) stream_KW_TINYINT.add(KW_TINYINT140);


                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    if ( backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 319:24: -> TOK_TINYINT
                    {
                        adaptor.addChild(root_0, adaptor.create(TOK_TINYINT, "TOK_TINYINT"));

                    }

                    }

                    }
                    break;
                case 2 :
                    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:320:7: KW_INT
                    {
                    KW_INT141=(Token)input.LT(1);
                    match(input,KW_INT,FOLLOW_KW_INT_in_primitiveType2099); if (failed) return retval;
                    if ( backtracking==0 ) stream_KW_INT.add(KW_INT141);


                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    if ( backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 320:24: -> TOK_INT
                    {
                        adaptor.addChild(root_0, adaptor.create(TOK_INT, "TOK_INT"));

                    }

                    }

                    }
                    break;
                case 3 :
                    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:321:7: KW_BIGINT
                    {
                    KW_BIGINT142=(Token)input.LT(1);
                    match(input,KW_BIGINT,FOLLOW_KW_BIGINT_in_primitiveType2124); if (failed) return retval;
                    if ( backtracking==0 ) stream_KW_BIGINT.add(KW_BIGINT142);


                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    if ( backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 321:24: -> TOK_BIGINT
                    {
                        adaptor.addChild(root_0, adaptor.create(TOK_BIGINT, "TOK_BIGINT"));

                    }

                    }

                    }
                    break;
                case 4 :
                    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:322:7: KW_BOOLEAN
                    {
                    KW_BOOLEAN143=(Token)input.LT(1);
                    match(input,KW_BOOLEAN,FOLLOW_KW_BOOLEAN_in_primitiveType2146); if (failed) return retval;
                    if ( backtracking==0 ) stream_KW_BOOLEAN.add(KW_BOOLEAN143);


                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    if ( backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 322:24: -> TOK_BOOLEAN
                    {
                        adaptor.addChild(root_0, adaptor.create(TOK_BOOLEAN, "TOK_BOOLEAN"));

                    }

                    }

                    }
                    break;
                case 5 :
                    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:323:7: KW_FLOAT
                    {
                    KW_FLOAT144=(Token)input.LT(1);
                    match(input,KW_FLOAT,FOLLOW_KW_FLOAT_in_primitiveType2167); if (failed) return retval;
                    if ( backtracking==0 ) stream_KW_FLOAT.add(KW_FLOAT144);


                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    if ( backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 323:24: -> TOK_FLOAT
                    {
                        adaptor.addChild(root_0, adaptor.create(TOK_FLOAT, "TOK_FLOAT"));

                    }

                    }

                    }
                    break;
                case 6 :
                    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:324:7: KW_DOUBLE
                    {
                    KW_DOUBLE145=(Token)input.LT(1);
                    match(input,KW_DOUBLE,FOLLOW_KW_DOUBLE_in_primitiveType2190); if (failed) return retval;
                    if ( backtracking==0 ) stream_KW_DOUBLE.add(KW_DOUBLE145);


                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    if ( backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 324:24: -> TOK_DOUBLE
                    {
                        adaptor.addChild(root_0, adaptor.create(TOK_DOUBLE, "TOK_DOUBLE"));

                    }

                    }

                    }
                    break;
                case 7 :
                    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:325:7: KW_DATE
                    {
                    KW_DATE146=(Token)input.LT(1);
                    match(input,KW_DATE,FOLLOW_KW_DATE_in_primitiveType2212); if (failed) return retval;
                    if ( backtracking==0 ) stream_KW_DATE.add(KW_DATE146);


                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    if ( backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 325:24: -> TOK_DATE
                    {
                        adaptor.addChild(root_0, adaptor.create(TOK_DATE, "TOK_DATE"));

                    }

                    }

                    }
                    break;
                case 8 :
                    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:326:7: KW_DATETIME
                    {
                    KW_DATETIME147=(Token)input.LT(1);
                    match(input,KW_DATETIME,FOLLOW_KW_DATETIME_in_primitiveType2236); if (failed) return retval;
                    if ( backtracking==0 ) stream_KW_DATETIME.add(KW_DATETIME147);


                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    if ( backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 326:24: -> TOK_DATETIME
                    {
                        adaptor.addChild(root_0, adaptor.create(TOK_DATETIME, "TOK_DATETIME"));

                    }

                    }

                    }
                    break;
                case 9 :
                    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:327:7: KW_TIMESTAMP
                    {
                    KW_TIMESTAMP148=(Token)input.LT(1);
                    match(input,KW_TIMESTAMP,FOLLOW_KW_TIMESTAMP_in_primitiveType2256); if (failed) return retval;
                    if ( backtracking==0 ) stream_KW_TIMESTAMP.add(KW_TIMESTAMP148);


                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    if ( backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 327:24: -> TOK_TIMESTAMP
                    {
                        adaptor.addChild(root_0, adaptor.create(TOK_TIMESTAMP, "TOK_TIMESTAMP"));

                    }

                    }

                    }
                    break;
                case 10 :
                    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:328:7: KW_STRING
                    {
                    KW_STRING149=(Token)input.LT(1);
                    match(input,KW_STRING,FOLLOW_KW_STRING_in_primitiveType2275); if (failed) return retval;
                    if ( backtracking==0 ) stream_KW_STRING.add(KW_STRING149);


                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    if ( backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 328:24: -> TOK_STRING
                    {
                        adaptor.addChild(root_0, adaptor.create(TOK_STRING, "TOK_STRING"));

                    }

                    }

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e) {
          reportError(e);
          throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end primitiveType

    public static class listType_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start listType
    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:331:1: listType : KW_ARRAY LESSTHAN primitiveType GREATERTHAN -> ^( TOK_LIST primitiveType ) ;
    public final listType_return listType() throws RecognitionException {
        listType_return retval = new listType_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token KW_ARRAY150=null;
        Token LESSTHAN151=null;
        Token GREATERTHAN153=null;
        primitiveType_return primitiveType152 = null;


        CommonTree KW_ARRAY150_tree=null;
        CommonTree LESSTHAN151_tree=null;
        CommonTree GREATERTHAN153_tree=null;
        RewriteRuleTokenStream stream_LESSTHAN=new RewriteRuleTokenStream(adaptor,"token LESSTHAN");
        RewriteRuleTokenStream stream_KW_ARRAY=new RewriteRuleTokenStream(adaptor,"token KW_ARRAY");
        RewriteRuleTokenStream stream_GREATERTHAN=new RewriteRuleTokenStream(adaptor,"token GREATERTHAN");
        RewriteRuleSubtreeStream stream_primitiveType=new RewriteRuleSubtreeStream(adaptor,"rule primitiveType");
        try {
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:332:5: ( KW_ARRAY LESSTHAN primitiveType GREATERTHAN -> ^( TOK_LIST primitiveType ) )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:332:7: KW_ARRAY LESSTHAN primitiveType GREATERTHAN
            {
            KW_ARRAY150=(Token)input.LT(1);
            match(input,KW_ARRAY,FOLLOW_KW_ARRAY_in_listType2306); if (failed) return retval;
            if ( backtracking==0 ) stream_KW_ARRAY.add(KW_ARRAY150);

            LESSTHAN151=(Token)input.LT(1);
            match(input,LESSTHAN,FOLLOW_LESSTHAN_in_listType2308); if (failed) return retval;
            if ( backtracking==0 ) stream_LESSTHAN.add(LESSTHAN151);

            pushFollow(FOLLOW_primitiveType_in_listType2310);
            primitiveType152=primitiveType();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_primitiveType.add(primitiveType152.getTree());
            GREATERTHAN153=(Token)input.LT(1);
            match(input,GREATERTHAN,FOLLOW_GREATERTHAN_in_listType2312); if (failed) return retval;
            if ( backtracking==0 ) stream_GREATERTHAN.add(GREATERTHAN153);


            // AST REWRITE
            // elements: primitiveType
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 332:53: -> ^( TOK_LIST primitiveType )
            {
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:332:56: ^( TOK_LIST primitiveType )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(adaptor.create(TOK_LIST, "TOK_LIST"), root_1);

                adaptor.addChild(root_1, stream_primitiveType.next());

                adaptor.addChild(root_0, root_1);
                }

            }

            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e) {
          reportError(e);
          throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end listType

    public static class mapType_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start mapType
    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:335:1: mapType : KW_MAP LESSTHAN left= primitiveType COMMA right= primitiveType GREATERTHAN -> ^( TOK_MAP $left $right) ;
    public final mapType_return mapType() throws RecognitionException {
        mapType_return retval = new mapType_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token KW_MAP154=null;
        Token LESSTHAN155=null;
        Token COMMA156=null;
        Token GREATERTHAN157=null;
        primitiveType_return left = null;

        primitiveType_return right = null;


        CommonTree KW_MAP154_tree=null;
        CommonTree LESSTHAN155_tree=null;
        CommonTree COMMA156_tree=null;
        CommonTree GREATERTHAN157_tree=null;
        RewriteRuleTokenStream stream_LESSTHAN=new RewriteRuleTokenStream(adaptor,"token LESSTHAN");
        RewriteRuleTokenStream stream_KW_MAP=new RewriteRuleTokenStream(adaptor,"token KW_MAP");
        RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
        RewriteRuleTokenStream stream_GREATERTHAN=new RewriteRuleTokenStream(adaptor,"token GREATERTHAN");
        RewriteRuleSubtreeStream stream_primitiveType=new RewriteRuleSubtreeStream(adaptor,"rule primitiveType");
        try {
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:336:5: ( KW_MAP LESSTHAN left= primitiveType COMMA right= primitiveType GREATERTHAN -> ^( TOK_MAP $left $right) )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:336:7: KW_MAP LESSTHAN left= primitiveType COMMA right= primitiveType GREATERTHAN
            {
            KW_MAP154=(Token)input.LT(1);
            match(input,KW_MAP,FOLLOW_KW_MAP_in_mapType2339); if (failed) return retval;
            if ( backtracking==0 ) stream_KW_MAP.add(KW_MAP154);

            LESSTHAN155=(Token)input.LT(1);
            match(input,LESSTHAN,FOLLOW_LESSTHAN_in_mapType2341); if (failed) return retval;
            if ( backtracking==0 ) stream_LESSTHAN.add(LESSTHAN155);

            pushFollow(FOLLOW_primitiveType_in_mapType2345);
            left=primitiveType();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_primitiveType.add(left.getTree());
            COMMA156=(Token)input.LT(1);
            match(input,COMMA,FOLLOW_COMMA_in_mapType2347); if (failed) return retval;
            if ( backtracking==0 ) stream_COMMA.add(COMMA156);

            pushFollow(FOLLOW_primitiveType_in_mapType2351);
            right=primitiveType();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_primitiveType.add(right.getTree());
            GREATERTHAN157=(Token)input.LT(1);
            match(input,GREATERTHAN,FOLLOW_GREATERTHAN_in_mapType2353); if (failed) return retval;
            if ( backtracking==0 ) stream_GREATERTHAN.add(GREATERTHAN157);


            // AST REWRITE
            // elements: left, right
            // token labels: 
            // rule labels: retval, left, right
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);
            RewriteRuleSubtreeStream stream_left=new RewriteRuleSubtreeStream(adaptor,"token left",left!=null?left.tree:null);
            RewriteRuleSubtreeStream stream_right=new RewriteRuleSubtreeStream(adaptor,"token right",right!=null?right.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 337:5: -> ^( TOK_MAP $left $right)
            {
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:337:8: ^( TOK_MAP $left $right)
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(adaptor.create(TOK_MAP, "TOK_MAP"), root_1);

                adaptor.addChild(root_1, stream_left.next());
                adaptor.addChild(root_1, stream_right.next());

                adaptor.addChild(root_0, root_1);
                }

            }

            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e) {
          reportError(e);
          throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end mapType

    public static class queryOperator_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start queryOperator
    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:340:1: queryOperator : KW_UNION KW_ALL -> ^( TOK_UNION ) ;
    public final queryOperator_return queryOperator() throws RecognitionException {
        queryOperator_return retval = new queryOperator_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token KW_UNION158=null;
        Token KW_ALL159=null;

        CommonTree KW_UNION158_tree=null;
        CommonTree KW_ALL159_tree=null;
        RewriteRuleTokenStream stream_KW_ALL=new RewriteRuleTokenStream(adaptor,"token KW_ALL");
        RewriteRuleTokenStream stream_KW_UNION=new RewriteRuleTokenStream(adaptor,"token KW_UNION");

        try {
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:341:5: ( KW_UNION KW_ALL -> ^( TOK_UNION ) )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:341:7: KW_UNION KW_ALL
            {
            KW_UNION158=(Token)input.LT(1);
            match(input,KW_UNION,FOLLOW_KW_UNION_in_queryOperator2386); if (failed) return retval;
            if ( backtracking==0 ) stream_KW_UNION.add(KW_UNION158);

            KW_ALL159=(Token)input.LT(1);
            match(input,KW_ALL,FOLLOW_KW_ALL_in_queryOperator2388); if (failed) return retval;
            if ( backtracking==0 ) stream_KW_ALL.add(KW_ALL159);


            // AST REWRITE
            // elements: 
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 341:23: -> ^( TOK_UNION )
            {
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:341:26: ^( TOK_UNION )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(adaptor.create(TOK_UNION, "TOK_UNION"), root_1);

                adaptor.addChild(root_0, root_1);
                }

            }

            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e) {
          reportError(e);
          throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end queryOperator

    public static class queryStatementExpression_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start queryStatementExpression
    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:345:1: queryStatementExpression : queryStatement ( queryOperator queryStatementExpression )* ;
    public final queryStatementExpression_return queryStatementExpression() throws RecognitionException {
        queryStatementExpression_return retval = new queryStatementExpression_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        queryStatement_return queryStatement160 = null;

        queryOperator_return queryOperator161 = null;

        queryStatementExpression_return queryStatementExpression162 = null;



        try {
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:346:5: ( queryStatement ( queryOperator queryStatementExpression )* )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:346:7: queryStatement ( queryOperator queryStatementExpression )*
            {
            root_0 = (CommonTree)adaptor.nil();

            pushFollow(FOLLOW_queryStatement_in_queryStatementExpression2412);
            queryStatement160=queryStatement();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) adaptor.addChild(root_0, queryStatement160.getTree());
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:346:22: ( queryOperator queryStatementExpression )*
            loop35:
            do {
                int alt35=2;
                int LA35_0 = input.LA(1);

                if ( (LA35_0==KW_UNION) ) {
                    int LA35_2 = input.LA(2);

                    if ( (synpred52()) ) {
                        alt35=1;
                    }


                }


                switch (alt35) {
            	case 1 :
            	    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:346:23: queryOperator queryStatementExpression
            	    {
            	    pushFollow(FOLLOW_queryOperator_in_queryStatementExpression2415);
            	    queryOperator161=queryOperator();
            	    _fsp--;
            	    if (failed) return retval;
            	    if ( backtracking==0 ) root_0 = (CommonTree)adaptor.becomeRoot(queryOperator161.getTree(), root_0);
            	    pushFollow(FOLLOW_queryStatementExpression_in_queryStatementExpression2418);
            	    queryStatementExpression162=queryStatementExpression();
            	    _fsp--;
            	    if (failed) return retval;
            	    if ( backtracking==0 ) adaptor.addChild(root_0, queryStatementExpression162.getTree());

            	    }
            	    break;

            	default :
            	    break loop35;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e) {
          reportError(e);
          throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end queryStatementExpression

    public static class queryStatement_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start queryStatement
    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:349:1: queryStatement : ( fromClause (b+= body )+ -> ^( TOK_QUERY fromClause ( body )+ ) | regular_body );
    public final queryStatement_return queryStatement() throws RecognitionException {
        queryStatement_return retval = new queryStatement_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        List list_b=null;
        fromClause_return fromClause163 = null;

        regular_body_return regular_body164 = null;

        RuleReturnScope b = null;
        RewriteRuleSubtreeStream stream_body=new RewriteRuleSubtreeStream(adaptor,"rule body");
        RewriteRuleSubtreeStream stream_fromClause=new RewriteRuleSubtreeStream(adaptor,"rule fromClause");
        try {
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:350:5: ( fromClause (b+= body )+ -> ^( TOK_QUERY fromClause ( body )+ ) | regular_body )
            int alt37=2;
            int LA37_0 = input.LA(1);

            if ( (LA37_0==KW_FROM) ) {
                alt37=1;
            }
            else if ( (LA37_0==KW_INSERT||LA37_0==KW_SELECT) ) {
                alt37=2;
            }
            else {
                if (backtracking>0) {failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("349:1: queryStatement : ( fromClause (b+= body )+ -> ^( TOK_QUERY fromClause ( body )+ ) | regular_body );", 37, 0, input);

                throw nvae;
            }
            switch (alt37) {
                case 1 :
                    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:351:5: fromClause (b+= body )+
                    {
                    pushFollow(FOLLOW_fromClause_in_queryStatement2441);
                    fromClause163=fromClause();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_fromClause.add(fromClause163.getTree());
                    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:352:5: (b+= body )+
                    int cnt36=0;
                    loop36:
                    do {
                        int alt36=2;
                        int LA36_0 = input.LA(1);

                        if ( (LA36_0==KW_INSERT||LA36_0==KW_SELECT) ) {
                            alt36=1;
                        }


                        switch (alt36) {
                    	case 1 :
                    	    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:352:7: b+= body
                    	    {
                    	    pushFollow(FOLLOW_body_in_queryStatement2451);
                    	    b=body();
                    	    _fsp--;
                    	    if (failed) return retval;
                    	    if ( backtracking==0 ) stream_body.add(b.getTree());
                    	    if (list_b==null) list_b=new ArrayList();
                    	    list_b.add(b);


                    	    }
                    	    break;

                    	default :
                    	    if ( cnt36 >= 1 ) break loop36;
                    	    if (backtracking>0) {failed=true; return retval;}
                                EarlyExitException eee =
                                    new EarlyExitException(36, input);
                                throw eee;
                        }
                        cnt36++;
                    } while (true);


                    // AST REWRITE
                    // elements: body, fromClause
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    if ( backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 352:18: -> ^( TOK_QUERY fromClause ( body )+ )
                    {
                        // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:352:21: ^( TOK_QUERY fromClause ( body )+ )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot(adaptor.create(TOK_QUERY, "TOK_QUERY"), root_1);

                        adaptor.addChild(root_1, stream_fromClause.next());
                        if ( !(stream_body.hasNext()) ) {
                            throw new RewriteEarlyExitException();
                        }
                        while ( stream_body.hasNext() ) {
                            adaptor.addChild(root_1, stream_body.next());

                        }
                        stream_body.reset();

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    }

                    }
                    break;
                case 2 :
                    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:353:7: regular_body
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_regular_body_in_queryStatement2473);
                    regular_body164=regular_body();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, regular_body164.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e) {
          reportError(e);
          throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end queryStatement

    public static class regular_body_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start regular_body
    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:356:1: regular_body : ( insertClause selectClause fromClause ( whereClause )? ( groupByClause )? ( orderByClause )? ( clusterByClause )? ( limitClause )? -> ^( TOK_QUERY fromClause ^( TOK_INSERT insertClause selectClause ( whereClause )? ( groupByClause )? ( orderByClause )? ( clusterByClause )? ( limitClause )? ) ) | selectClause fromClause ( whereClause )? ( groupByClause )? ( orderByClause )? ( clusterByClause )? ( limitClause )? -> ^( TOK_QUERY fromClause ^( TOK_INSERT ^( TOK_DESTINATION ^( TOK_DIR TOK_TMP_FILE ) ) selectClause ( whereClause )? ( groupByClause )? ( orderByClause )? ( clusterByClause )? ( limitClause )? ) ) );
    public final regular_body_return regular_body() throws RecognitionException {
        regular_body_return retval = new regular_body_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        insertClause_return insertClause165 = null;

        selectClause_return selectClause166 = null;

        fromClause_return fromClause167 = null;

        whereClause_return whereClause168 = null;

        groupByClause_return groupByClause169 = null;

        orderByClause_return orderByClause170 = null;

        clusterByClause_return clusterByClause171 = null;

        limitClause_return limitClause172 = null;

        selectClause_return selectClause173 = null;

        fromClause_return fromClause174 = null;

        whereClause_return whereClause175 = null;

        groupByClause_return groupByClause176 = null;

        orderByClause_return orderByClause177 = null;

        clusterByClause_return clusterByClause178 = null;

        limitClause_return limitClause179 = null;


        RewriteRuleSubtreeStream stream_whereClause=new RewriteRuleSubtreeStream(adaptor,"rule whereClause");
        RewriteRuleSubtreeStream stream_clusterByClause=new RewriteRuleSubtreeStream(adaptor,"rule clusterByClause");
        RewriteRuleSubtreeStream stream_limitClause=new RewriteRuleSubtreeStream(adaptor,"rule limitClause");
        RewriteRuleSubtreeStream stream_orderByClause=new RewriteRuleSubtreeStream(adaptor,"rule orderByClause");
        RewriteRuleSubtreeStream stream_insertClause=new RewriteRuleSubtreeStream(adaptor,"rule insertClause");
        RewriteRuleSubtreeStream stream_groupByClause=new RewriteRuleSubtreeStream(adaptor,"rule groupByClause");
        RewriteRuleSubtreeStream stream_selectClause=new RewriteRuleSubtreeStream(adaptor,"rule selectClause");
        RewriteRuleSubtreeStream stream_fromClause=new RewriteRuleSubtreeStream(adaptor,"rule fromClause");
        try {
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:357:4: ( insertClause selectClause fromClause ( whereClause )? ( groupByClause )? ( orderByClause )? ( clusterByClause )? ( limitClause )? -> ^( TOK_QUERY fromClause ^( TOK_INSERT insertClause selectClause ( whereClause )? ( groupByClause )? ( orderByClause )? ( clusterByClause )? ( limitClause )? ) ) | selectClause fromClause ( whereClause )? ( groupByClause )? ( orderByClause )? ( clusterByClause )? ( limitClause )? -> ^( TOK_QUERY fromClause ^( TOK_INSERT ^( TOK_DESTINATION ^( TOK_DIR TOK_TMP_FILE ) ) selectClause ( whereClause )? ( groupByClause )? ( orderByClause )? ( clusterByClause )? ( limitClause )? ) ) )
            int alt48=2;
            int LA48_0 = input.LA(1);

            if ( (LA48_0==KW_INSERT) ) {
                alt48=1;
            }
            else if ( (LA48_0==KW_SELECT) ) {
                alt48=2;
            }
            else {
                if (backtracking>0) {failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("356:1: regular_body : ( insertClause selectClause fromClause ( whereClause )? ( groupByClause )? ( orderByClause )? ( clusterByClause )? ( limitClause )? -> ^( TOK_QUERY fromClause ^( TOK_INSERT insertClause selectClause ( whereClause )? ( groupByClause )? ( orderByClause )? ( clusterByClause )? ( limitClause )? ) ) | selectClause fromClause ( whereClause )? ( groupByClause )? ( orderByClause )? ( clusterByClause )? ( limitClause )? -> ^( TOK_QUERY fromClause ^( TOK_INSERT ^( TOK_DESTINATION ^( TOK_DIR TOK_TMP_FILE ) ) selectClause ( whereClause )? ( groupByClause )? ( orderByClause )? ( clusterByClause )? ( limitClause )? ) ) );", 48, 0, input);

                throw nvae;
            }
            switch (alt48) {
                case 1 :
                    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:358:4: insertClause selectClause fromClause ( whereClause )? ( groupByClause )? ( orderByClause )? ( clusterByClause )? ( limitClause )?
                    {
                    pushFollow(FOLLOW_insertClause_in_regular_body2492);
                    insertClause165=insertClause();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_insertClause.add(insertClause165.getTree());
                    pushFollow(FOLLOW_selectClause_in_regular_body2497);
                    selectClause166=selectClause();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_selectClause.add(selectClause166.getTree());
                    pushFollow(FOLLOW_fromClause_in_regular_body2502);
                    fromClause167=fromClause();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_fromClause.add(fromClause167.getTree());
                    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:361:4: ( whereClause )?
                    int alt38=2;
                    int LA38_0 = input.LA(1);

                    if ( (LA38_0==KW_WHERE) ) {
                        alt38=1;
                    }
                    switch (alt38) {
                        case 1 :
                            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:0:0: whereClause
                            {
                            pushFollow(FOLLOW_whereClause_in_regular_body2507);
                            whereClause168=whereClause();
                            _fsp--;
                            if (failed) return retval;
                            if ( backtracking==0 ) stream_whereClause.add(whereClause168.getTree());

                            }
                            break;

                    }

                    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:362:4: ( groupByClause )?
                    int alt39=2;
                    int LA39_0 = input.LA(1);

                    if ( (LA39_0==KW_GROUP) ) {
                        alt39=1;
                    }
                    switch (alt39) {
                        case 1 :
                            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:0:0: groupByClause
                            {
                            pushFollow(FOLLOW_groupByClause_in_regular_body2513);
                            groupByClause169=groupByClause();
                            _fsp--;
                            if (failed) return retval;
                            if ( backtracking==0 ) stream_groupByClause.add(groupByClause169.getTree());

                            }
                            break;

                    }

                    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:363:4: ( orderByClause )?
                    int alt40=2;
                    int LA40_0 = input.LA(1);

                    if ( (LA40_0==KW_ORDER) ) {
                        alt40=1;
                    }
                    switch (alt40) {
                        case 1 :
                            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:0:0: orderByClause
                            {
                            pushFollow(FOLLOW_orderByClause_in_regular_body2519);
                            orderByClause170=orderByClause();
                            _fsp--;
                            if (failed) return retval;
                            if ( backtracking==0 ) stream_orderByClause.add(orderByClause170.getTree());

                            }
                            break;

                    }

                    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:364:4: ( clusterByClause )?
                    int alt41=2;
                    int LA41_0 = input.LA(1);

                    if ( (LA41_0==KW_CLUSTER) ) {
                        alt41=1;
                    }
                    switch (alt41) {
                        case 1 :
                            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:0:0: clusterByClause
                            {
                            pushFollow(FOLLOW_clusterByClause_in_regular_body2525);
                            clusterByClause171=clusterByClause();
                            _fsp--;
                            if (failed) return retval;
                            if ( backtracking==0 ) stream_clusterByClause.add(clusterByClause171.getTree());

                            }
                            break;

                    }

                    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:365:4: ( limitClause )?
                    int alt42=2;
                    int LA42_0 = input.LA(1);

                    if ( (LA42_0==KW_LIMIT) ) {
                        alt42=1;
                    }
                    switch (alt42) {
                        case 1 :
                            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:0:0: limitClause
                            {
                            pushFollow(FOLLOW_limitClause_in_regular_body2532);
                            limitClause172=limitClause();
                            _fsp--;
                            if (failed) return retval;
                            if ( backtracking==0 ) stream_limitClause.add(limitClause172.getTree());

                            }
                            break;

                    }


                    // AST REWRITE
                    // elements: fromClause, whereClause, selectClause, orderByClause, clusterByClause, insertClause, limitClause, groupByClause
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    if ( backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 365:17: -> ^( TOK_QUERY fromClause ^( TOK_INSERT insertClause selectClause ( whereClause )? ( groupByClause )? ( orderByClause )? ( clusterByClause )? ( limitClause )? ) )
                    {
                        // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:365:20: ^( TOK_QUERY fromClause ^( TOK_INSERT insertClause selectClause ( whereClause )? ( groupByClause )? ( orderByClause )? ( clusterByClause )? ( limitClause )? ) )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot(adaptor.create(TOK_QUERY, "TOK_QUERY"), root_1);

                        adaptor.addChild(root_1, stream_fromClause.next());
                        // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:365:43: ^( TOK_INSERT insertClause selectClause ( whereClause )? ( groupByClause )? ( orderByClause )? ( clusterByClause )? ( limitClause )? )
                        {
                        CommonTree root_2 = (CommonTree)adaptor.nil();
                        root_2 = (CommonTree)adaptor.becomeRoot(adaptor.create(TOK_INSERT, "TOK_INSERT"), root_2);

                        adaptor.addChild(root_2, stream_insertClause.next());
                        adaptor.addChild(root_2, stream_selectClause.next());
                        // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:365:82: ( whereClause )?
                        if ( stream_whereClause.hasNext() ) {
                            adaptor.addChild(root_2, stream_whereClause.next());

                        }
                        stream_whereClause.reset();
                        // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:365:95: ( groupByClause )?
                        if ( stream_groupByClause.hasNext() ) {
                            adaptor.addChild(root_2, stream_groupByClause.next());

                        }
                        stream_groupByClause.reset();
                        // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:365:110: ( orderByClause )?
                        if ( stream_orderByClause.hasNext() ) {
                            adaptor.addChild(root_2, stream_orderByClause.next());

                        }
                        stream_orderByClause.reset();
                        // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:365:125: ( clusterByClause )?
                        if ( stream_clusterByClause.hasNext() ) {
                            adaptor.addChild(root_2, stream_clusterByClause.next());

                        }
                        stream_clusterByClause.reset();
                        // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:365:142: ( limitClause )?
                        if ( stream_limitClause.hasNext() ) {
                            adaptor.addChild(root_2, stream_limitClause.next());

                        }
                        stream_limitClause.reset();

                        adaptor.addChild(root_1, root_2);
                        }

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    }

                    }
                    break;
                case 2 :
                    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:367:4: selectClause fromClause ( whereClause )? ( groupByClause )? ( orderByClause )? ( clusterByClause )? ( limitClause )?
                    {
                    pushFollow(FOLLOW_selectClause_in_regular_body2574);
                    selectClause173=selectClause();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_selectClause.add(selectClause173.getTree());
                    pushFollow(FOLLOW_fromClause_in_regular_body2579);
                    fromClause174=fromClause();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_fromClause.add(fromClause174.getTree());
                    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:369:4: ( whereClause )?
                    int alt43=2;
                    int LA43_0 = input.LA(1);

                    if ( (LA43_0==KW_WHERE) ) {
                        alt43=1;
                    }
                    switch (alt43) {
                        case 1 :
                            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:0:0: whereClause
                            {
                            pushFollow(FOLLOW_whereClause_in_regular_body2584);
                            whereClause175=whereClause();
                            _fsp--;
                            if (failed) return retval;
                            if ( backtracking==0 ) stream_whereClause.add(whereClause175.getTree());

                            }
                            break;

                    }

                    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:370:4: ( groupByClause )?
                    int alt44=2;
                    int LA44_0 = input.LA(1);

                    if ( (LA44_0==KW_GROUP) ) {
                        alt44=1;
                    }
                    switch (alt44) {
                        case 1 :
                            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:0:0: groupByClause
                            {
                            pushFollow(FOLLOW_groupByClause_in_regular_body2590);
                            groupByClause176=groupByClause();
                            _fsp--;
                            if (failed) return retval;
                            if ( backtracking==0 ) stream_groupByClause.add(groupByClause176.getTree());

                            }
                            break;

                    }

                    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:371:4: ( orderByClause )?
                    int alt45=2;
                    int LA45_0 = input.LA(1);

                    if ( (LA45_0==KW_ORDER) ) {
                        alt45=1;
                    }
                    switch (alt45) {
                        case 1 :
                            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:0:0: orderByClause
                            {
                            pushFollow(FOLLOW_orderByClause_in_regular_body2596);
                            orderByClause177=orderByClause();
                            _fsp--;
                            if (failed) return retval;
                            if ( backtracking==0 ) stream_orderByClause.add(orderByClause177.getTree());

                            }
                            break;

                    }

                    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:372:4: ( clusterByClause )?
                    int alt46=2;
                    int LA46_0 = input.LA(1);

                    if ( (LA46_0==KW_CLUSTER) ) {
                        alt46=1;
                    }
                    switch (alt46) {
                        case 1 :
                            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:0:0: clusterByClause
                            {
                            pushFollow(FOLLOW_clusterByClause_in_regular_body2602);
                            clusterByClause178=clusterByClause();
                            _fsp--;
                            if (failed) return retval;
                            if ( backtracking==0 ) stream_clusterByClause.add(clusterByClause178.getTree());

                            }
                            break;

                    }

                    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:373:4: ( limitClause )?
                    int alt47=2;
                    int LA47_0 = input.LA(1);

                    if ( (LA47_0==KW_LIMIT) ) {
                        alt47=1;
                    }
                    switch (alt47) {
                        case 1 :
                            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:0:0: limitClause
                            {
                            pushFollow(FOLLOW_limitClause_in_regular_body2609);
                            limitClause179=limitClause();
                            _fsp--;
                            if (failed) return retval;
                            if ( backtracking==0 ) stream_limitClause.add(limitClause179.getTree());

                            }
                            break;

                    }


                    // AST REWRITE
                    // elements: orderByClause, limitClause, clusterByClause, groupByClause, selectClause, fromClause, whereClause
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    if ( backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 373:17: -> ^( TOK_QUERY fromClause ^( TOK_INSERT ^( TOK_DESTINATION ^( TOK_DIR TOK_TMP_FILE ) ) selectClause ( whereClause )? ( groupByClause )? ( orderByClause )? ( clusterByClause )? ( limitClause )? ) )
                    {
                        // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:373:20: ^( TOK_QUERY fromClause ^( TOK_INSERT ^( TOK_DESTINATION ^( TOK_DIR TOK_TMP_FILE ) ) selectClause ( whereClause )? ( groupByClause )? ( orderByClause )? ( clusterByClause )? ( limitClause )? ) )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot(adaptor.create(TOK_QUERY, "TOK_QUERY"), root_1);

                        adaptor.addChild(root_1, stream_fromClause.next());
                        // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:373:43: ^( TOK_INSERT ^( TOK_DESTINATION ^( TOK_DIR TOK_TMP_FILE ) ) selectClause ( whereClause )? ( groupByClause )? ( orderByClause )? ( clusterByClause )? ( limitClause )? )
                        {
                        CommonTree root_2 = (CommonTree)adaptor.nil();
                        root_2 = (CommonTree)adaptor.becomeRoot(adaptor.create(TOK_INSERT, "TOK_INSERT"), root_2);

                        // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:373:56: ^( TOK_DESTINATION ^( TOK_DIR TOK_TMP_FILE ) )
                        {
                        CommonTree root_3 = (CommonTree)adaptor.nil();
                        root_3 = (CommonTree)adaptor.becomeRoot(adaptor.create(TOK_DESTINATION, "TOK_DESTINATION"), root_3);

                        // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:373:74: ^( TOK_DIR TOK_TMP_FILE )
                        {
                        CommonTree root_4 = (CommonTree)adaptor.nil();
                        root_4 = (CommonTree)adaptor.becomeRoot(adaptor.create(TOK_DIR, "TOK_DIR"), root_4);

                        adaptor.addChild(root_4, adaptor.create(TOK_TMP_FILE, "TOK_TMP_FILE"));

                        adaptor.addChild(root_3, root_4);
                        }

                        adaptor.addChild(root_2, root_3);
                        }
                        adaptor.addChild(root_2, stream_selectClause.next());
                        // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:373:112: ( whereClause )?
                        if ( stream_whereClause.hasNext() ) {
                            adaptor.addChild(root_2, stream_whereClause.next());

                        }
                        stream_whereClause.reset();
                        // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:373:125: ( groupByClause )?
                        if ( stream_groupByClause.hasNext() ) {
                            adaptor.addChild(root_2, stream_groupByClause.next());

                        }
                        stream_groupByClause.reset();
                        // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:373:140: ( orderByClause )?
                        if ( stream_orderByClause.hasNext() ) {
                            adaptor.addChild(root_2, stream_orderByClause.next());

                        }
                        stream_orderByClause.reset();
                        // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:373:155: ( clusterByClause )?
                        if ( stream_clusterByClause.hasNext() ) {
                            adaptor.addChild(root_2, stream_clusterByClause.next());

                        }
                        stream_clusterByClause.reset();
                        // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:373:172: ( limitClause )?
                        if ( stream_limitClause.hasNext() ) {
                            adaptor.addChild(root_2, stream_limitClause.next());

                        }
                        stream_limitClause.reset();

                        adaptor.addChild(root_1, root_2);
                        }

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    }

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e) {
          reportError(e);
          throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end regular_body

    public static class body_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start body
    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:377:1: body : ( insertClause selectClause ( whereClause )? ( groupByClause )? ( orderByClause )? ( clusterByClause )? ( limitClause )? -> ^( TOK_INSERT ( insertClause )? selectClause ( whereClause )? ( groupByClause )? ( orderByClause )? ( clusterByClause )? ( limitClause )? ) | selectClause ( whereClause )? ( groupByClause )? ( orderByClause )? ( clusterByClause )? ( limitClause )? -> ^( TOK_INSERT ^( TOK_DESTINATION ^( TOK_DIR TOK_TMP_FILE ) ) selectClause ( whereClause )? ( groupByClause )? ( orderByClause )? ( clusterByClause )? ( limitClause )? ) );
    public final body_return body() throws RecognitionException {
        body_return retval = new body_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        insertClause_return insertClause180 = null;

        selectClause_return selectClause181 = null;

        whereClause_return whereClause182 = null;

        groupByClause_return groupByClause183 = null;

        orderByClause_return orderByClause184 = null;

        clusterByClause_return clusterByClause185 = null;

        limitClause_return limitClause186 = null;

        selectClause_return selectClause187 = null;

        whereClause_return whereClause188 = null;

        groupByClause_return groupByClause189 = null;

        orderByClause_return orderByClause190 = null;

        clusterByClause_return clusterByClause191 = null;

        limitClause_return limitClause192 = null;


        RewriteRuleSubtreeStream stream_whereClause=new RewriteRuleSubtreeStream(adaptor,"rule whereClause");
        RewriteRuleSubtreeStream stream_clusterByClause=new RewriteRuleSubtreeStream(adaptor,"rule clusterByClause");
        RewriteRuleSubtreeStream stream_limitClause=new RewriteRuleSubtreeStream(adaptor,"rule limitClause");
        RewriteRuleSubtreeStream stream_orderByClause=new RewriteRuleSubtreeStream(adaptor,"rule orderByClause");
        RewriteRuleSubtreeStream stream_insertClause=new RewriteRuleSubtreeStream(adaptor,"rule insertClause");
        RewriteRuleSubtreeStream stream_groupByClause=new RewriteRuleSubtreeStream(adaptor,"rule groupByClause");
        RewriteRuleSubtreeStream stream_selectClause=new RewriteRuleSubtreeStream(adaptor,"rule selectClause");
        try {
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:378:4: ( insertClause selectClause ( whereClause )? ( groupByClause )? ( orderByClause )? ( clusterByClause )? ( limitClause )? -> ^( TOK_INSERT ( insertClause )? selectClause ( whereClause )? ( groupByClause )? ( orderByClause )? ( clusterByClause )? ( limitClause )? ) | selectClause ( whereClause )? ( groupByClause )? ( orderByClause )? ( clusterByClause )? ( limitClause )? -> ^( TOK_INSERT ^( TOK_DESTINATION ^( TOK_DIR TOK_TMP_FILE ) ) selectClause ( whereClause )? ( groupByClause )? ( orderByClause )? ( clusterByClause )? ( limitClause )? ) )
            int alt59=2;
            int LA59_0 = input.LA(1);

            if ( (LA59_0==KW_INSERT) ) {
                alt59=1;
            }
            else if ( (LA59_0==KW_SELECT) ) {
                alt59=2;
            }
            else {
                if (backtracking>0) {failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("377:1: body : ( insertClause selectClause ( whereClause )? ( groupByClause )? ( orderByClause )? ( clusterByClause )? ( limitClause )? -> ^( TOK_INSERT ( insertClause )? selectClause ( whereClause )? ( groupByClause )? ( orderByClause )? ( clusterByClause )? ( limitClause )? ) | selectClause ( whereClause )? ( groupByClause )? ( orderByClause )? ( clusterByClause )? ( limitClause )? -> ^( TOK_INSERT ^( TOK_DESTINATION ^( TOK_DIR TOK_TMP_FILE ) ) selectClause ( whereClause )? ( groupByClause )? ( orderByClause )? ( clusterByClause )? ( limitClause )? ) );", 59, 0, input);

                throw nvae;
            }
            switch (alt59) {
                case 1 :
                    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:379:4: insertClause selectClause ( whereClause )? ( groupByClause )? ( orderByClause )? ( clusterByClause )? ( limitClause )?
                    {
                    pushFollow(FOLLOW_insertClause_in_body2668);
                    insertClause180=insertClause();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_insertClause.add(insertClause180.getTree());
                    pushFollow(FOLLOW_selectClause_in_body2673);
                    selectClause181=selectClause();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_selectClause.add(selectClause181.getTree());
                    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:381:4: ( whereClause )?
                    int alt49=2;
                    int LA49_0 = input.LA(1);

                    if ( (LA49_0==KW_WHERE) ) {
                        alt49=1;
                    }
                    switch (alt49) {
                        case 1 :
                            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:0:0: whereClause
                            {
                            pushFollow(FOLLOW_whereClause_in_body2678);
                            whereClause182=whereClause();
                            _fsp--;
                            if (failed) return retval;
                            if ( backtracking==0 ) stream_whereClause.add(whereClause182.getTree());

                            }
                            break;

                    }

                    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:382:4: ( groupByClause )?
                    int alt50=2;
                    int LA50_0 = input.LA(1);

                    if ( (LA50_0==KW_GROUP) ) {
                        alt50=1;
                    }
                    switch (alt50) {
                        case 1 :
                            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:0:0: groupByClause
                            {
                            pushFollow(FOLLOW_groupByClause_in_body2684);
                            groupByClause183=groupByClause();
                            _fsp--;
                            if (failed) return retval;
                            if ( backtracking==0 ) stream_groupByClause.add(groupByClause183.getTree());

                            }
                            break;

                    }

                    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:383:4: ( orderByClause )?
                    int alt51=2;
                    int LA51_0 = input.LA(1);

                    if ( (LA51_0==KW_ORDER) ) {
                        alt51=1;
                    }
                    switch (alt51) {
                        case 1 :
                            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:0:0: orderByClause
                            {
                            pushFollow(FOLLOW_orderByClause_in_body2690);
                            orderByClause184=orderByClause();
                            _fsp--;
                            if (failed) return retval;
                            if ( backtracking==0 ) stream_orderByClause.add(orderByClause184.getTree());

                            }
                            break;

                    }

                    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:384:4: ( clusterByClause )?
                    int alt52=2;
                    int LA52_0 = input.LA(1);

                    if ( (LA52_0==KW_CLUSTER) ) {
                        alt52=1;
                    }
                    switch (alt52) {
                        case 1 :
                            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:0:0: clusterByClause
                            {
                            pushFollow(FOLLOW_clusterByClause_in_body2697);
                            clusterByClause185=clusterByClause();
                            _fsp--;
                            if (failed) return retval;
                            if ( backtracking==0 ) stream_clusterByClause.add(clusterByClause185.getTree());

                            }
                            break;

                    }

                    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:385:4: ( limitClause )?
                    int alt53=2;
                    int LA53_0 = input.LA(1);

                    if ( (LA53_0==KW_LIMIT) ) {
                        alt53=1;
                    }
                    switch (alt53) {
                        case 1 :
                            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:0:0: limitClause
                            {
                            pushFollow(FOLLOW_limitClause_in_body2704);
                            limitClause186=limitClause();
                            _fsp--;
                            if (failed) return retval;
                            if ( backtracking==0 ) stream_limitClause.add(limitClause186.getTree());

                            }
                            break;

                    }


                    // AST REWRITE
                    // elements: clusterByClause, groupByClause, selectClause, limitClause, orderByClause, whereClause, insertClause
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    if ( backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 385:17: -> ^( TOK_INSERT ( insertClause )? selectClause ( whereClause )? ( groupByClause )? ( orderByClause )? ( clusterByClause )? ( limitClause )? )
                    {
                        // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:385:20: ^( TOK_INSERT ( insertClause )? selectClause ( whereClause )? ( groupByClause )? ( orderByClause )? ( clusterByClause )? ( limitClause )? )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot(adaptor.create(TOK_INSERT, "TOK_INSERT"), root_1);

                        // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:385:33: ( insertClause )?
                        if ( stream_insertClause.hasNext() ) {
                            adaptor.addChild(root_1, stream_insertClause.next());

                        }
                        stream_insertClause.reset();
                        adaptor.addChild(root_1, stream_selectClause.next());
                        // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:385:60: ( whereClause )?
                        if ( stream_whereClause.hasNext() ) {
                            adaptor.addChild(root_1, stream_whereClause.next());

                        }
                        stream_whereClause.reset();
                        // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:385:73: ( groupByClause )?
                        if ( stream_groupByClause.hasNext() ) {
                            adaptor.addChild(root_1, stream_groupByClause.next());

                        }
                        stream_groupByClause.reset();
                        // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:385:88: ( orderByClause )?
                        if ( stream_orderByClause.hasNext() ) {
                            adaptor.addChild(root_1, stream_orderByClause.next());

                        }
                        stream_orderByClause.reset();
                        // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:385:103: ( clusterByClause )?
                        if ( stream_clusterByClause.hasNext() ) {
                            adaptor.addChild(root_1, stream_clusterByClause.next());

                        }
                        stream_clusterByClause.reset();
                        // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:385:120: ( limitClause )?
                        if ( stream_limitClause.hasNext() ) {
                            adaptor.addChild(root_1, stream_limitClause.next());

                        }
                        stream_limitClause.reset();

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    }

                    }
                    break;
                case 2 :
                    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:387:4: selectClause ( whereClause )? ( groupByClause )? ( orderByClause )? ( clusterByClause )? ( limitClause )?
                    {
                    pushFollow(FOLLOW_selectClause_in_body2741);
                    selectClause187=selectClause();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_selectClause.add(selectClause187.getTree());
                    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:388:4: ( whereClause )?
                    int alt54=2;
                    int LA54_0 = input.LA(1);

                    if ( (LA54_0==KW_WHERE) ) {
                        alt54=1;
                    }
                    switch (alt54) {
                        case 1 :
                            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:0:0: whereClause
                            {
                            pushFollow(FOLLOW_whereClause_in_body2746);
                            whereClause188=whereClause();
                            _fsp--;
                            if (failed) return retval;
                            if ( backtracking==0 ) stream_whereClause.add(whereClause188.getTree());

                            }
                            break;

                    }

                    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:389:4: ( groupByClause )?
                    int alt55=2;
                    int LA55_0 = input.LA(1);

                    if ( (LA55_0==KW_GROUP) ) {
                        alt55=1;
                    }
                    switch (alt55) {
                        case 1 :
                            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:0:0: groupByClause
                            {
                            pushFollow(FOLLOW_groupByClause_in_body2752);
                            groupByClause189=groupByClause();
                            _fsp--;
                            if (failed) return retval;
                            if ( backtracking==0 ) stream_groupByClause.add(groupByClause189.getTree());

                            }
                            break;

                    }

                    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:390:4: ( orderByClause )?
                    int alt56=2;
                    int LA56_0 = input.LA(1);

                    if ( (LA56_0==KW_ORDER) ) {
                        alt56=1;
                    }
                    switch (alt56) {
                        case 1 :
                            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:0:0: orderByClause
                            {
                            pushFollow(FOLLOW_orderByClause_in_body2758);
                            orderByClause190=orderByClause();
                            _fsp--;
                            if (failed) return retval;
                            if ( backtracking==0 ) stream_orderByClause.add(orderByClause190.getTree());

                            }
                            break;

                    }

                    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:391:4: ( clusterByClause )?
                    int alt57=2;
                    int LA57_0 = input.LA(1);

                    if ( (LA57_0==KW_CLUSTER) ) {
                        alt57=1;
                    }
                    switch (alt57) {
                        case 1 :
                            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:0:0: clusterByClause
                            {
                            pushFollow(FOLLOW_clusterByClause_in_body2765);
                            clusterByClause191=clusterByClause();
                            _fsp--;
                            if (failed) return retval;
                            if ( backtracking==0 ) stream_clusterByClause.add(clusterByClause191.getTree());

                            }
                            break;

                    }

                    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:392:4: ( limitClause )?
                    int alt58=2;
                    int LA58_0 = input.LA(1);

                    if ( (LA58_0==KW_LIMIT) ) {
                        alt58=1;
                    }
                    switch (alt58) {
                        case 1 :
                            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:0:0: limitClause
                            {
                            pushFollow(FOLLOW_limitClause_in_body2772);
                            limitClause192=limitClause();
                            _fsp--;
                            if (failed) return retval;
                            if ( backtracking==0 ) stream_limitClause.add(limitClause192.getTree());

                            }
                            break;

                    }


                    // AST REWRITE
                    // elements: limitClause, clusterByClause, orderByClause, groupByClause, whereClause, selectClause
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    if ( backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 392:17: -> ^( TOK_INSERT ^( TOK_DESTINATION ^( TOK_DIR TOK_TMP_FILE ) ) selectClause ( whereClause )? ( groupByClause )? ( orderByClause )? ( clusterByClause )? ( limitClause )? )
                    {
                        // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:392:20: ^( TOK_INSERT ^( TOK_DESTINATION ^( TOK_DIR TOK_TMP_FILE ) ) selectClause ( whereClause )? ( groupByClause )? ( orderByClause )? ( clusterByClause )? ( limitClause )? )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot(adaptor.create(TOK_INSERT, "TOK_INSERT"), root_1);

                        // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:392:33: ^( TOK_DESTINATION ^( TOK_DIR TOK_TMP_FILE ) )
                        {
                        CommonTree root_2 = (CommonTree)adaptor.nil();
                        root_2 = (CommonTree)adaptor.becomeRoot(adaptor.create(TOK_DESTINATION, "TOK_DESTINATION"), root_2);

                        // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:392:51: ^( TOK_DIR TOK_TMP_FILE )
                        {
                        CommonTree root_3 = (CommonTree)adaptor.nil();
                        root_3 = (CommonTree)adaptor.becomeRoot(adaptor.create(TOK_DIR, "TOK_DIR"), root_3);

                        adaptor.addChild(root_3, adaptor.create(TOK_TMP_FILE, "TOK_TMP_FILE"));

                        adaptor.addChild(root_2, root_3);
                        }

                        adaptor.addChild(root_1, root_2);
                        }
                        adaptor.addChild(root_1, stream_selectClause.next());
                        // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:392:89: ( whereClause )?
                        if ( stream_whereClause.hasNext() ) {
                            adaptor.addChild(root_1, stream_whereClause.next());

                        }
                        stream_whereClause.reset();
                        // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:392:102: ( groupByClause )?
                        if ( stream_groupByClause.hasNext() ) {
                            adaptor.addChild(root_1, stream_groupByClause.next());

                        }
                        stream_groupByClause.reset();
                        // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:392:117: ( orderByClause )?
                        if ( stream_orderByClause.hasNext() ) {
                            adaptor.addChild(root_1, stream_orderByClause.next());

                        }
                        stream_orderByClause.reset();
                        // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:392:132: ( clusterByClause )?
                        if ( stream_clusterByClause.hasNext() ) {
                            adaptor.addChild(root_1, stream_clusterByClause.next());

                        }
                        stream_clusterByClause.reset();
                        // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:392:149: ( limitClause )?
                        if ( stream_limitClause.hasNext() ) {
                            adaptor.addChild(root_1, stream_limitClause.next());

                        }
                        stream_limitClause.reset();

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    }

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e) {
          reportError(e);
          throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end body

    public static class insertClause_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start insertClause
    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:395:1: insertClause : KW_INSERT KW_OVERWRITE destination -> ^( TOK_DESTINATION destination ) ;
    public final insertClause_return insertClause() throws RecognitionException {
        insertClause_return retval = new insertClause_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token KW_INSERT193=null;
        Token KW_OVERWRITE194=null;
        destination_return destination195 = null;


        CommonTree KW_INSERT193_tree=null;
        CommonTree KW_OVERWRITE194_tree=null;
        RewriteRuleTokenStream stream_KW_OVERWRITE=new RewriteRuleTokenStream(adaptor,"token KW_OVERWRITE");
        RewriteRuleTokenStream stream_KW_INSERT=new RewriteRuleTokenStream(adaptor,"token KW_INSERT");
        RewriteRuleSubtreeStream stream_destination=new RewriteRuleSubtreeStream(adaptor,"rule destination");
        try {
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:396:4: ( KW_INSERT KW_OVERWRITE destination -> ^( TOK_DESTINATION destination ) )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:397:4: KW_INSERT KW_OVERWRITE destination
            {
            KW_INSERT193=(Token)input.LT(1);
            match(input,KW_INSERT,FOLLOW_KW_INSERT_in_insertClause2828); if (failed) return retval;
            if ( backtracking==0 ) stream_KW_INSERT.add(KW_INSERT193);

            KW_OVERWRITE194=(Token)input.LT(1);
            match(input,KW_OVERWRITE,FOLLOW_KW_OVERWRITE_in_insertClause2830); if (failed) return retval;
            if ( backtracking==0 ) stream_KW_OVERWRITE.add(KW_OVERWRITE194);

            pushFollow(FOLLOW_destination_in_insertClause2832);
            destination195=destination();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_destination.add(destination195.getTree());

            // AST REWRITE
            // elements: destination
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 397:39: -> ^( TOK_DESTINATION destination )
            {
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:397:42: ^( TOK_DESTINATION destination )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(adaptor.create(TOK_DESTINATION, "TOK_DESTINATION"), root_1);

                adaptor.addChild(root_1, stream_destination.next());

                adaptor.addChild(root_0, root_1);
                }

            }

            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e) {
          reportError(e);
          throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end insertClause

    public static class destination_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start destination
    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:400:1: destination : ( KW_LOCAL KW_DIRECTORY StringLiteral -> ^( TOK_LOCAL_DIR StringLiteral ) | KW_DIRECTORY StringLiteral -> ^( TOK_DIR StringLiteral ) | KW_TABLE tabName -> ^( tabName ) );
    public final destination_return destination() throws RecognitionException {
        destination_return retval = new destination_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token KW_LOCAL196=null;
        Token KW_DIRECTORY197=null;
        Token StringLiteral198=null;
        Token KW_DIRECTORY199=null;
        Token StringLiteral200=null;
        Token KW_TABLE201=null;
        tabName_return tabName202 = null;


        CommonTree KW_LOCAL196_tree=null;
        CommonTree KW_DIRECTORY197_tree=null;
        CommonTree StringLiteral198_tree=null;
        CommonTree KW_DIRECTORY199_tree=null;
        CommonTree StringLiteral200_tree=null;
        CommonTree KW_TABLE201_tree=null;
        RewriteRuleTokenStream stream_StringLiteral=new RewriteRuleTokenStream(adaptor,"token StringLiteral");
        RewriteRuleTokenStream stream_KW_DIRECTORY=new RewriteRuleTokenStream(adaptor,"token KW_DIRECTORY");
        RewriteRuleTokenStream stream_KW_LOCAL=new RewriteRuleTokenStream(adaptor,"token KW_LOCAL");
        RewriteRuleTokenStream stream_KW_TABLE=new RewriteRuleTokenStream(adaptor,"token KW_TABLE");
        RewriteRuleSubtreeStream stream_tabName=new RewriteRuleSubtreeStream(adaptor,"rule tabName");
        try {
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:401:4: ( KW_LOCAL KW_DIRECTORY StringLiteral -> ^( TOK_LOCAL_DIR StringLiteral ) | KW_DIRECTORY StringLiteral -> ^( TOK_DIR StringLiteral ) | KW_TABLE tabName -> ^( tabName ) )
            int alt60=3;
            switch ( input.LA(1) ) {
            case KW_LOCAL:
                {
                alt60=1;
                }
                break;
            case KW_DIRECTORY:
                {
                alt60=2;
                }
                break;
            case KW_TABLE:
                {
                alt60=3;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("400:1: destination : ( KW_LOCAL KW_DIRECTORY StringLiteral -> ^( TOK_LOCAL_DIR StringLiteral ) | KW_DIRECTORY StringLiteral -> ^( TOK_DIR StringLiteral ) | KW_TABLE tabName -> ^( tabName ) );", 60, 0, input);

                throw nvae;
            }

            switch (alt60) {
                case 1 :
                    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:402:6: KW_LOCAL KW_DIRECTORY StringLiteral
                    {
                    KW_LOCAL196=(Token)input.LT(1);
                    match(input,KW_LOCAL,FOLLOW_KW_LOCAL_in_destination2860); if (failed) return retval;
                    if ( backtracking==0 ) stream_KW_LOCAL.add(KW_LOCAL196);

                    KW_DIRECTORY197=(Token)input.LT(1);
                    match(input,KW_DIRECTORY,FOLLOW_KW_DIRECTORY_in_destination2862); if (failed) return retval;
                    if ( backtracking==0 ) stream_KW_DIRECTORY.add(KW_DIRECTORY197);

                    StringLiteral198=(Token)input.LT(1);
                    match(input,StringLiteral,FOLLOW_StringLiteral_in_destination2864); if (failed) return retval;
                    if ( backtracking==0 ) stream_StringLiteral.add(StringLiteral198);


                    // AST REWRITE
                    // elements: StringLiteral
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    if ( backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 402:42: -> ^( TOK_LOCAL_DIR StringLiteral )
                    {
                        // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:402:45: ^( TOK_LOCAL_DIR StringLiteral )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot(adaptor.create(TOK_LOCAL_DIR, "TOK_LOCAL_DIR"), root_1);

                        adaptor.addChild(root_1, stream_StringLiteral.next());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    }

                    }
                    break;
                case 2 :
                    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:403:6: KW_DIRECTORY StringLiteral
                    {
                    KW_DIRECTORY199=(Token)input.LT(1);
                    match(input,KW_DIRECTORY,FOLLOW_KW_DIRECTORY_in_destination2879); if (failed) return retval;
                    if ( backtracking==0 ) stream_KW_DIRECTORY.add(KW_DIRECTORY199);

                    StringLiteral200=(Token)input.LT(1);
                    match(input,StringLiteral,FOLLOW_StringLiteral_in_destination2881); if (failed) return retval;
                    if ( backtracking==0 ) stream_StringLiteral.add(StringLiteral200);


                    // AST REWRITE
                    // elements: StringLiteral
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    if ( backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 403:33: -> ^( TOK_DIR StringLiteral )
                    {
                        // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:403:36: ^( TOK_DIR StringLiteral )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot(adaptor.create(TOK_DIR, "TOK_DIR"), root_1);

                        adaptor.addChild(root_1, stream_StringLiteral.next());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    }

                    }
                    break;
                case 3 :
                    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:404:6: KW_TABLE tabName
                    {
                    KW_TABLE201=(Token)input.LT(1);
                    match(input,KW_TABLE,FOLLOW_KW_TABLE_in_destination2896); if (failed) return retval;
                    if ( backtracking==0 ) stream_KW_TABLE.add(KW_TABLE201);

                    pushFollow(FOLLOW_tabName_in_destination2898);
                    tabName202=tabName();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_tabName.add(tabName202.getTree());

                    // AST REWRITE
                    // elements: tabName
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    if ( backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 404:23: -> ^( tabName )
                    {
                        // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:404:26: ^( tabName )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot(stream_tabName.nextNode(), root_1);

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    }

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e) {
          reportError(e);
          throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end destination

    public static class limitClause_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start limitClause
    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:407:1: limitClause : KW_LIMIT num= Number -> ^( TOK_LIMIT $num) ;
    public final limitClause_return limitClause() throws RecognitionException {
        limitClause_return retval = new limitClause_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token num=null;
        Token KW_LIMIT203=null;

        CommonTree num_tree=null;
        CommonTree KW_LIMIT203_tree=null;
        RewriteRuleTokenStream stream_Number=new RewriteRuleTokenStream(adaptor,"token Number");
        RewriteRuleTokenStream stream_KW_LIMIT=new RewriteRuleTokenStream(adaptor,"token KW_LIMIT");

        try {
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:408:4: ( KW_LIMIT num= Number -> ^( TOK_LIMIT $num) )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:409:4: KW_LIMIT num= Number
            {
            KW_LIMIT203=(Token)input.LT(1);
            match(input,KW_LIMIT,FOLLOW_KW_LIMIT_in_limitClause2922); if (failed) return retval;
            if ( backtracking==0 ) stream_KW_LIMIT.add(KW_LIMIT203);

            num=(Token)input.LT(1);
            match(input,Number,FOLLOW_Number_in_limitClause2926); if (failed) return retval;
            if ( backtracking==0 ) stream_Number.add(num);


            // AST REWRITE
            // elements: num
            // token labels: num
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleTokenStream stream_num=new RewriteRuleTokenStream(adaptor,"token num",num);
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 409:24: -> ^( TOK_LIMIT $num)
            {
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:409:27: ^( TOK_LIMIT $num)
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(adaptor.create(TOK_LIMIT, "TOK_LIMIT"), root_1);

                adaptor.addChild(root_1, stream_num.next());

                adaptor.addChild(root_0, root_1);
                }

            }

            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e) {
          reportError(e);
          throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end limitClause

    public static class selectClause_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start selectClause
    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:414:1: selectClause : KW_SELECT ( KW_ALL | dist= KW_DISTINCT )? selectList -> {$dist == null}? ^( TOK_SELECT selectList ) -> ^( TOK_SELECTDI selectList ) ;
    public final selectClause_return selectClause() throws RecognitionException {
        selectClause_return retval = new selectClause_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token dist=null;
        Token KW_SELECT204=null;
        Token KW_ALL205=null;
        selectList_return selectList206 = null;


        CommonTree dist_tree=null;
        CommonTree KW_SELECT204_tree=null;
        CommonTree KW_ALL205_tree=null;
        RewriteRuleTokenStream stream_KW_ALL=new RewriteRuleTokenStream(adaptor,"token KW_ALL");
        RewriteRuleTokenStream stream_KW_SELECT=new RewriteRuleTokenStream(adaptor,"token KW_SELECT");
        RewriteRuleTokenStream stream_KW_DISTINCT=new RewriteRuleTokenStream(adaptor,"token KW_DISTINCT");
        RewriteRuleSubtreeStream stream_selectList=new RewriteRuleSubtreeStream(adaptor,"rule selectList");
        try {
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:415:5: ( KW_SELECT ( KW_ALL | dist= KW_DISTINCT )? selectList -> {$dist == null}? ^( TOK_SELECT selectList ) -> ^( TOK_SELECTDI selectList ) )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:416:5: KW_SELECT ( KW_ALL | dist= KW_DISTINCT )? selectList
            {
            KW_SELECT204=(Token)input.LT(1);
            match(input,KW_SELECT,FOLLOW_KW_SELECT_in_selectClause2957); if (failed) return retval;
            if ( backtracking==0 ) stream_KW_SELECT.add(KW_SELECT204);

            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:416:15: ( KW_ALL | dist= KW_DISTINCT )?
            int alt61=3;
            int LA61_0 = input.LA(1);

            if ( (LA61_0==KW_ALL) ) {
                alt61=1;
            }
            else if ( (LA61_0==KW_DISTINCT) ) {
                alt61=2;
            }
            switch (alt61) {
                case 1 :
                    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:416:16: KW_ALL
                    {
                    KW_ALL205=(Token)input.LT(1);
                    match(input,KW_ALL,FOLLOW_KW_ALL_in_selectClause2960); if (failed) return retval;
                    if ( backtracking==0 ) stream_KW_ALL.add(KW_ALL205);


                    }
                    break;
                case 2 :
                    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:416:25: dist= KW_DISTINCT
                    {
                    dist=(Token)input.LT(1);
                    match(input,KW_DISTINCT,FOLLOW_KW_DISTINCT_in_selectClause2966); if (failed) return retval;
                    if ( backtracking==0 ) stream_KW_DISTINCT.add(dist);


                    }
                    break;

            }

            pushFollow(FOLLOW_selectList_in_selectClause2974);
            selectList206=selectList();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_selectList.add(selectList206.getTree());

            // AST REWRITE
            // elements: selectList, selectList
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 417:16: -> {$dist == null}? ^( TOK_SELECT selectList )
            if (dist == null) {
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:417:36: ^( TOK_SELECT selectList )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(adaptor.create(TOK_SELECT, "TOK_SELECT"), root_1);

                adaptor.addChild(root_1, stream_selectList.next());

                adaptor.addChild(root_0, root_1);
                }

            }
            else // 418:16: -> ^( TOK_SELECTDI selectList )
            {
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:418:36: ^( TOK_SELECTDI selectList )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(adaptor.create(TOK_SELECTDI, "TOK_SELECTDI"), root_1);

                adaptor.addChild(root_1, stream_selectList.next());

                adaptor.addChild(root_0, root_1);
                }

            }

            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e) {
          reportError(e);
          throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end selectClause

    public static class selectList_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start selectList
    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:421:1: selectList : selectItem ( COMMA selectItem )* -> ( selectItem )+ ;
    public final selectList_return selectList() throws RecognitionException {
        selectList_return retval = new selectList_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token COMMA208=null;
        selectItem_return selectItem207 = null;

        selectItem_return selectItem209 = null;


        CommonTree COMMA208_tree=null;
        RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
        RewriteRuleSubtreeStream stream_selectItem=new RewriteRuleSubtreeStream(adaptor,"rule selectItem");
        try {
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:422:5: ( selectItem ( COMMA selectItem )* -> ( selectItem )+ )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:423:5: selectItem ( COMMA selectItem )*
            {
            pushFollow(FOLLOW_selectItem_in_selectList3045);
            selectItem207=selectItem();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_selectItem.add(selectItem207.getTree());
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:424:5: ( COMMA selectItem )*
            loop62:
            do {
                int alt62=2;
                int LA62_0 = input.LA(1);

                if ( (LA62_0==COMMA) ) {
                    alt62=1;
                }


                switch (alt62) {
            	case 1 :
            	    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:424:7: COMMA selectItem
            	    {
            	    COMMA208=(Token)input.LT(1);
            	    match(input,COMMA,FOLLOW_COMMA_in_selectList3053); if (failed) return retval;
            	    if ( backtracking==0 ) stream_COMMA.add(COMMA208);

            	    pushFollow(FOLLOW_selectItem_in_selectList3056);
            	    selectItem209=selectItem();
            	    _fsp--;
            	    if (failed) return retval;
            	    if ( backtracking==0 ) stream_selectItem.add(selectItem209.getTree());

            	    }
            	    break;

            	default :
            	    break loop62;
                }
            } while (true);


            // AST REWRITE
            // elements: selectItem
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 424:28: -> ( selectItem )+
            {
                if ( !(stream_selectItem.hasNext()) ) {
                    throw new RewriteEarlyExitException();
                }
                while ( stream_selectItem.hasNext() ) {
                    adaptor.addChild(root_0, stream_selectItem.next());

                }
                stream_selectItem.reset();

            }

            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e) {
          reportError(e);
          throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end selectList

    public static class selectItem_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start selectItem
    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:427:1: selectItem : ( trfmClause -> ^( TOK_SELEXPR trfmClause ) | ( selectExpression ( KW_AS Identifier )? ) -> ^( TOK_SELEXPR selectExpression ( Identifier )? ) );
    public final selectItem_return selectItem() throws RecognitionException {
        selectItem_return retval = new selectItem_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token KW_AS212=null;
        Token Identifier213=null;
        trfmClause_return trfmClause210 = null;

        selectExpression_return selectExpression211 = null;


        CommonTree KW_AS212_tree=null;
        CommonTree Identifier213_tree=null;
        RewriteRuleTokenStream stream_KW_AS=new RewriteRuleTokenStream(adaptor,"token KW_AS");
        RewriteRuleTokenStream stream_Identifier=new RewriteRuleTokenStream(adaptor,"token Identifier");
        RewriteRuleSubtreeStream stream_selectExpression=new RewriteRuleSubtreeStream(adaptor,"rule selectExpression");
        RewriteRuleSubtreeStream stream_trfmClause=new RewriteRuleSubtreeStream(adaptor,"rule trfmClause");
        try {
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:428:5: ( trfmClause -> ^( TOK_SELEXPR trfmClause ) | ( selectExpression ( KW_AS Identifier )? ) -> ^( TOK_SELEXPR selectExpression ( Identifier )? ) )
            int alt64=2;
            int LA64_0 = input.LA(1);

            if ( (LA64_0==KW_TRANSFORM) ) {
                alt64=1;
            }
            else if ( (LA64_0==StringLiteral||(LA64_0>=Identifier && LA64_0<=LPAREN)||LA64_0==Number||LA64_0==DOT||(LA64_0>=KW_CAST && LA64_0<=CharSetName)||LA64_0==KW_NULL||(LA64_0>=PLUS && LA64_0<=TILDE)||LA64_0==KW_NOT||(LA64_0>=KW_TRUE && LA64_0<=KW_FALSE)) ) {
                alt64=2;
            }
            else {
                if (backtracking>0) {failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("427:1: selectItem : ( trfmClause -> ^( TOK_SELEXPR trfmClause ) | ( selectExpression ( KW_AS Identifier )? ) -> ^( TOK_SELEXPR selectExpression ( Identifier )? ) );", 64, 0, input);

                throw nvae;
            }
            switch (alt64) {
                case 1 :
                    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:429:7: trfmClause
                    {
                    pushFollow(FOLLOW_trfmClause_in_selectItem3087);
                    trfmClause210=trfmClause();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_trfmClause.add(trfmClause210.getTree());

                    // AST REWRITE
                    // elements: trfmClause
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    if ( backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 429:18: -> ^( TOK_SELEXPR trfmClause )
                    {
                        // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:429:21: ^( TOK_SELEXPR trfmClause )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot(adaptor.create(TOK_SELEXPR, "TOK_SELEXPR"), root_1);

                        adaptor.addChild(root_1, stream_trfmClause.next());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    }

                    }
                    break;
                case 2 :
                    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:430:7: ( selectExpression ( KW_AS Identifier )? )
                    {
                    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:430:7: ( selectExpression ( KW_AS Identifier )? )
                    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:430:8: selectExpression ( KW_AS Identifier )?
                    {
                    pushFollow(FOLLOW_selectExpression_in_selectItem3104);
                    selectExpression211=selectExpression();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_selectExpression.add(selectExpression211.getTree());
                    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:430:26: ( KW_AS Identifier )?
                    int alt63=2;
                    int LA63_0 = input.LA(1);

                    if ( (LA63_0==KW_AS) ) {
                        alt63=1;
                    }
                    switch (alt63) {
                        case 1 :
                            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:430:27: KW_AS Identifier
                            {
                            KW_AS212=(Token)input.LT(1);
                            match(input,KW_AS,FOLLOW_KW_AS_in_selectItem3108); if (failed) return retval;
                            if ( backtracking==0 ) stream_KW_AS.add(KW_AS212);

                            Identifier213=(Token)input.LT(1);
                            match(input,Identifier,FOLLOW_Identifier_in_selectItem3110); if (failed) return retval;
                            if ( backtracking==0 ) stream_Identifier.add(Identifier213);


                            }
                            break;

                    }


                    }


                    // AST REWRITE
                    // elements: selectExpression, Identifier
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    if ( backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 430:47: -> ^( TOK_SELEXPR selectExpression ( Identifier )? )
                    {
                        // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:430:50: ^( TOK_SELEXPR selectExpression ( Identifier )? )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot(adaptor.create(TOK_SELEXPR, "TOK_SELEXPR"), root_1);

                        adaptor.addChild(root_1, stream_selectExpression.next());
                        // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:430:81: ( Identifier )?
                        if ( stream_Identifier.hasNext() ) {
                            adaptor.addChild(root_1, stream_Identifier.next());

                        }
                        stream_Identifier.reset();

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    }

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e) {
          reportError(e);
          throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end selectItem

    public static class trfmClause_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start trfmClause
    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:433:1: trfmClause : KW_TRANSFORM LPAREN columnList RPAREN KW_AS LPAREN aliasList RPAREN KW_USING StringLiteral -> ^( TOK_TRANSFORM columnList aliasList StringLiteral ) ;
    public final trfmClause_return trfmClause() throws RecognitionException {
        trfmClause_return retval = new trfmClause_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token KW_TRANSFORM214=null;
        Token LPAREN215=null;
        Token RPAREN217=null;
        Token KW_AS218=null;
        Token LPAREN219=null;
        Token RPAREN221=null;
        Token KW_USING222=null;
        Token StringLiteral223=null;
        columnList_return columnList216 = null;

        aliasList_return aliasList220 = null;


        CommonTree KW_TRANSFORM214_tree=null;
        CommonTree LPAREN215_tree=null;
        CommonTree RPAREN217_tree=null;
        CommonTree KW_AS218_tree=null;
        CommonTree LPAREN219_tree=null;
        CommonTree RPAREN221_tree=null;
        CommonTree KW_USING222_tree=null;
        CommonTree StringLiteral223_tree=null;
        RewriteRuleTokenStream stream_StringLiteral=new RewriteRuleTokenStream(adaptor,"token StringLiteral");
        RewriteRuleTokenStream stream_KW_AS=new RewriteRuleTokenStream(adaptor,"token KW_AS");
        RewriteRuleTokenStream stream_RPAREN=new RewriteRuleTokenStream(adaptor,"token RPAREN");
        RewriteRuleTokenStream stream_KW_USING=new RewriteRuleTokenStream(adaptor,"token KW_USING");
        RewriteRuleTokenStream stream_KW_TRANSFORM=new RewriteRuleTokenStream(adaptor,"token KW_TRANSFORM");
        RewriteRuleTokenStream stream_LPAREN=new RewriteRuleTokenStream(adaptor,"token LPAREN");
        RewriteRuleSubtreeStream stream_aliasList=new RewriteRuleSubtreeStream(adaptor,"rule aliasList");
        RewriteRuleSubtreeStream stream_columnList=new RewriteRuleSubtreeStream(adaptor,"rule columnList");
        try {
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:434:5: ( KW_TRANSFORM LPAREN columnList RPAREN KW_AS LPAREN aliasList RPAREN KW_USING StringLiteral -> ^( TOK_TRANSFORM columnList aliasList StringLiteral ) )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:435:5: KW_TRANSFORM LPAREN columnList RPAREN KW_AS LPAREN aliasList RPAREN KW_USING StringLiteral
            {
            KW_TRANSFORM214=(Token)input.LT(1);
            match(input,KW_TRANSFORM,FOLLOW_KW_TRANSFORM_in_trfmClause3149); if (failed) return retval;
            if ( backtracking==0 ) stream_KW_TRANSFORM.add(KW_TRANSFORM214);

            LPAREN215=(Token)input.LT(1);
            match(input,LPAREN,FOLLOW_LPAREN_in_trfmClause3155); if (failed) return retval;
            if ( backtracking==0 ) stream_LPAREN.add(LPAREN215);

            pushFollow(FOLLOW_columnList_in_trfmClause3157);
            columnList216=columnList();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_columnList.add(columnList216.getTree());
            RPAREN217=(Token)input.LT(1);
            match(input,RPAREN,FOLLOW_RPAREN_in_trfmClause3159); if (failed) return retval;
            if ( backtracking==0 ) stream_RPAREN.add(RPAREN217);

            KW_AS218=(Token)input.LT(1);
            match(input,KW_AS,FOLLOW_KW_AS_in_trfmClause3165); if (failed) return retval;
            if ( backtracking==0 ) stream_KW_AS.add(KW_AS218);

            LPAREN219=(Token)input.LT(1);
            match(input,LPAREN,FOLLOW_LPAREN_in_trfmClause3172); if (failed) return retval;
            if ( backtracking==0 ) stream_LPAREN.add(LPAREN219);

            pushFollow(FOLLOW_aliasList_in_trfmClause3174);
            aliasList220=aliasList();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_aliasList.add(aliasList220.getTree());
            RPAREN221=(Token)input.LT(1);
            match(input,RPAREN,FOLLOW_RPAREN_in_trfmClause3176); if (failed) return retval;
            if ( backtracking==0 ) stream_RPAREN.add(RPAREN221);

            KW_USING222=(Token)input.LT(1);
            match(input,KW_USING,FOLLOW_KW_USING_in_trfmClause3182); if (failed) return retval;
            if ( backtracking==0 ) stream_KW_USING.add(KW_USING222);

            StringLiteral223=(Token)input.LT(1);
            match(input,StringLiteral,FOLLOW_StringLiteral_in_trfmClause3184); if (failed) return retval;
            if ( backtracking==0 ) stream_StringLiteral.add(StringLiteral223);


            // AST REWRITE
            // elements: columnList, StringLiteral, aliasList
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 439:28: -> ^( TOK_TRANSFORM columnList aliasList StringLiteral )
            {
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:439:31: ^( TOK_TRANSFORM columnList aliasList StringLiteral )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(adaptor.create(TOK_TRANSFORM, "TOK_TRANSFORM"), root_1);

                adaptor.addChild(root_1, stream_columnList.next());
                adaptor.addChild(root_1, stream_aliasList.next());
                adaptor.addChild(root_1, stream_StringLiteral.next());

                adaptor.addChild(root_0, root_1);
                }

            }

            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e) {
          reportError(e);
          throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end trfmClause

    public static class selectExpression_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start selectExpression
    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:442:1: selectExpression : ( expression | tableAllColumns );
    public final selectExpression_return selectExpression() throws RecognitionException {
        selectExpression_return retval = new selectExpression_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        expression_return expression224 = null;

        tableAllColumns_return tableAllColumns225 = null;



        try {
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:443:5: ( expression | tableAllColumns )
            int alt65=2;
            int LA65_0 = input.LA(1);

            if ( (LA65_0==StringLiteral||LA65_0==LPAREN||LA65_0==Number||LA65_0==DOT||(LA65_0>=KW_CAST && LA65_0<=CharSetName)||LA65_0==KW_NULL||(LA65_0>=PLUS && LA65_0<=TILDE)||LA65_0==KW_NOT||(LA65_0>=KW_TRUE && LA65_0<=KW_FALSE)) ) {
                alt65=1;
            }
            else if ( (LA65_0==Identifier) ) {
                int LA65_2 = input.LA(2);

                if ( (LA65_2==LPAREN) ) {
                    alt65=1;
                }
                else if ( (LA65_2==DOT) ) {
                    int LA65_3 = input.LA(3);

                    if ( (LA65_3==Identifier) ) {
                        alt65=1;
                    }
                    else if ( (LA65_3==STAR) ) {
                        alt65=2;
                    }
                    else {
                        if (backtracking>0) {failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("442:1: selectExpression : ( expression | tableAllColumns );", 65, 3, input);

                        throw nvae;
                    }
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("442:1: selectExpression : ( expression | tableAllColumns );", 65, 2, input);

                    throw nvae;
                }
            }
            else {
                if (backtracking>0) {failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("442:1: selectExpression : ( expression | tableAllColumns );", 65, 0, input);

                throw nvae;
            }
            switch (alt65) {
                case 1 :
                    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:444:5: expression
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_expression_in_selectExpression3221);
                    expression224=expression();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, expression224.getTree());

                    }
                    break;
                case 2 :
                    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:444:18: tableAllColumns
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_tableAllColumns_in_selectExpression3225);
                    tableAllColumns225=tableAllColumns();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, tableAllColumns225.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e) {
          reportError(e);
          throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end selectExpression

    public static class tableAllColumns_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start tableAllColumns
    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:449:1: tableAllColumns : Identifier DOT STAR -> ^( TOK_ALLCOLREF Identifier ) ;
    public final tableAllColumns_return tableAllColumns() throws RecognitionException {
        tableAllColumns_return retval = new tableAllColumns_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token Identifier226=null;
        Token DOT227=null;
        Token STAR228=null;

        CommonTree Identifier226_tree=null;
        CommonTree DOT227_tree=null;
        CommonTree STAR228_tree=null;
        RewriteRuleTokenStream stream_STAR=new RewriteRuleTokenStream(adaptor,"token STAR");
        RewriteRuleTokenStream stream_DOT=new RewriteRuleTokenStream(adaptor,"token DOT");
        RewriteRuleTokenStream stream_Identifier=new RewriteRuleTokenStream(adaptor,"token Identifier");

        try {
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:450:5: ( Identifier DOT STAR -> ^( TOK_ALLCOLREF Identifier ) )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:451:5: Identifier DOT STAR
            {
            Identifier226=(Token)input.LT(1);
            match(input,Identifier,FOLLOW_Identifier_in_tableAllColumns3248); if (failed) return retval;
            if ( backtracking==0 ) stream_Identifier.add(Identifier226);

            DOT227=(Token)input.LT(1);
            match(input,DOT,FOLLOW_DOT_in_tableAllColumns3250); if (failed) return retval;
            if ( backtracking==0 ) stream_DOT.add(DOT227);

            STAR228=(Token)input.LT(1);
            match(input,STAR,FOLLOW_STAR_in_tableAllColumns3252); if (failed) return retval;
            if ( backtracking==0 ) stream_STAR.add(STAR228);


            // AST REWRITE
            // elements: Identifier
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 451:25: -> ^( TOK_ALLCOLREF Identifier )
            {
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:451:28: ^( TOK_ALLCOLREF Identifier )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(adaptor.create(TOK_ALLCOLREF, "TOK_ALLCOLREF"), root_1);

                adaptor.addChild(root_1, stream_Identifier.next());

                adaptor.addChild(root_0, root_1);
                }

            }

            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e) {
          reportError(e);
          throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end tableAllColumns

    public static class tableColumn_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start tableColumn
    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:455:1: tableColumn : (tab= Identifier )? DOT col= Identifier -> ^( TOK_COLREF ( $tab)? $col) ;
    public final tableColumn_return tableColumn() throws RecognitionException {
        tableColumn_return retval = new tableColumn_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token tab=null;
        Token col=null;
        Token DOT229=null;

        CommonTree tab_tree=null;
        CommonTree col_tree=null;
        CommonTree DOT229_tree=null;
        RewriteRuleTokenStream stream_DOT=new RewriteRuleTokenStream(adaptor,"token DOT");
        RewriteRuleTokenStream stream_Identifier=new RewriteRuleTokenStream(adaptor,"token Identifier");

        try {
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:456:5: ( (tab= Identifier )? DOT col= Identifier -> ^( TOK_COLREF ( $tab)? $col) )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:457:5: (tab= Identifier )? DOT col= Identifier
            {
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:457:5: (tab= Identifier )?
            int alt66=2;
            int LA66_0 = input.LA(1);

            if ( (LA66_0==Identifier) ) {
                alt66=1;
            }
            switch (alt66) {
                case 1 :
                    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:457:6: tab= Identifier
                    {
                    tab=(Token)input.LT(1);
                    match(input,Identifier,FOLLOW_Identifier_in_tableColumn3289); if (failed) return retval;
                    if ( backtracking==0 ) stream_Identifier.add(tab);


                    }
                    break;

            }

            DOT229=(Token)input.LT(1);
            match(input,DOT,FOLLOW_DOT_in_tableColumn3293); if (failed) return retval;
            if ( backtracking==0 ) stream_DOT.add(DOT229);

            col=(Token)input.LT(1);
            match(input,Identifier,FOLLOW_Identifier_in_tableColumn3297); if (failed) return retval;
            if ( backtracking==0 ) stream_Identifier.add(col);


            // AST REWRITE
            // elements: tab, col
            // token labels: col, tab
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleTokenStream stream_col=new RewriteRuleTokenStream(adaptor,"token col",col);
            RewriteRuleTokenStream stream_tab=new RewriteRuleTokenStream(adaptor,"token tab",tab);
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 457:42: -> ^( TOK_COLREF ( $tab)? $col)
            {
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:457:45: ^( TOK_COLREF ( $tab)? $col)
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(adaptor.create(TOK_COLREF, "TOK_COLREF"), root_1);

                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:457:58: ( $tab)?
                if ( stream_tab.hasNext() ) {
                    adaptor.addChild(root_1, stream_tab.next());

                }
                stream_tab.reset();
                adaptor.addChild(root_1, stream_col.next());

                adaptor.addChild(root_0, root_1);
                }

            }

            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e) {
          reportError(e);
          throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end tableColumn

    public static class columnList_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start columnList
    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:460:1: columnList : tableColumn ( COMMA tableColumn )* -> ^( TOK_COLLIST ( tableColumn )+ ) ;
    public final columnList_return columnList() throws RecognitionException {
        columnList_return retval = new columnList_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token COMMA231=null;
        tableColumn_return tableColumn230 = null;

        tableColumn_return tableColumn232 = null;


        CommonTree COMMA231_tree=null;
        RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
        RewriteRuleSubtreeStream stream_tableColumn=new RewriteRuleSubtreeStream(adaptor,"rule tableColumn");
        try {
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:461:5: ( tableColumn ( COMMA tableColumn )* -> ^( TOK_COLLIST ( tableColumn )+ ) )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:462:5: tableColumn ( COMMA tableColumn )*
            {
            pushFollow(FOLLOW_tableColumn_in_columnList3331);
            tableColumn230=tableColumn();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_tableColumn.add(tableColumn230.getTree());
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:462:17: ( COMMA tableColumn )*
            loop67:
            do {
                int alt67=2;
                int LA67_0 = input.LA(1);

                if ( (LA67_0==COMMA) ) {
                    alt67=1;
                }


                switch (alt67) {
            	case 1 :
            	    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:462:18: COMMA tableColumn
            	    {
            	    COMMA231=(Token)input.LT(1);
            	    match(input,COMMA,FOLLOW_COMMA_in_columnList3334); if (failed) return retval;
            	    if ( backtracking==0 ) stream_COMMA.add(COMMA231);

            	    pushFollow(FOLLOW_tableColumn_in_columnList3336);
            	    tableColumn232=tableColumn();
            	    _fsp--;
            	    if (failed) return retval;
            	    if ( backtracking==0 ) stream_tableColumn.add(tableColumn232.getTree());

            	    }
            	    break;

            	default :
            	    break loop67;
                }
            } while (true);


            // AST REWRITE
            // elements: tableColumn
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 462:38: -> ^( TOK_COLLIST ( tableColumn )+ )
            {
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:462:41: ^( TOK_COLLIST ( tableColumn )+ )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(adaptor.create(TOK_COLLIST, "TOK_COLLIST"), root_1);

                if ( !(stream_tableColumn.hasNext()) ) {
                    throw new RewriteEarlyExitException();
                }
                while ( stream_tableColumn.hasNext() ) {
                    adaptor.addChild(root_1, stream_tableColumn.next());

                }
                stream_tableColumn.reset();

                adaptor.addChild(root_0, root_1);
                }

            }

            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e) {
          reportError(e);
          throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end columnList

    public static class aliasList_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start aliasList
    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:465:1: aliasList : Identifier ( COMMA Identifier )* -> ^( TOK_ALIASLIST ( Identifier )+ ) ;
    public final aliasList_return aliasList() throws RecognitionException {
        aliasList_return retval = new aliasList_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token Identifier233=null;
        Token COMMA234=null;
        Token Identifier235=null;

        CommonTree Identifier233_tree=null;
        CommonTree COMMA234_tree=null;
        CommonTree Identifier235_tree=null;
        RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
        RewriteRuleTokenStream stream_Identifier=new RewriteRuleTokenStream(adaptor,"token Identifier");

        try {
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:466:5: ( Identifier ( COMMA Identifier )* -> ^( TOK_ALIASLIST ( Identifier )+ ) )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:467:5: Identifier ( COMMA Identifier )*
            {
            Identifier233=(Token)input.LT(1);
            match(input,Identifier,FOLLOW_Identifier_in_aliasList3368); if (failed) return retval;
            if ( backtracking==0 ) stream_Identifier.add(Identifier233);

            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:467:16: ( COMMA Identifier )*
            loop68:
            do {
                int alt68=2;
                int LA68_0 = input.LA(1);

                if ( (LA68_0==COMMA) ) {
                    alt68=1;
                }


                switch (alt68) {
            	case 1 :
            	    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:467:17: COMMA Identifier
            	    {
            	    COMMA234=(Token)input.LT(1);
            	    match(input,COMMA,FOLLOW_COMMA_in_aliasList3371); if (failed) return retval;
            	    if ( backtracking==0 ) stream_COMMA.add(COMMA234);

            	    Identifier235=(Token)input.LT(1);
            	    match(input,Identifier,FOLLOW_Identifier_in_aliasList3373); if (failed) return retval;
            	    if ( backtracking==0 ) stream_Identifier.add(Identifier235);


            	    }
            	    break;

            	default :
            	    break loop68;
                }
            } while (true);


            // AST REWRITE
            // elements: Identifier
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 467:36: -> ^( TOK_ALIASLIST ( Identifier )+ )
            {
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:467:39: ^( TOK_ALIASLIST ( Identifier )+ )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(adaptor.create(TOK_ALIASLIST, "TOK_ALIASLIST"), root_1);

                if ( !(stream_Identifier.hasNext()) ) {
                    throw new RewriteEarlyExitException();
                }
                while ( stream_Identifier.hasNext() ) {
                    adaptor.addChild(root_1, stream_Identifier.next());

                }
                stream_Identifier.reset();

                adaptor.addChild(root_0, root_1);
                }

            }

            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e) {
          reportError(e);
          throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end aliasList

    public static class fromClause_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start fromClause
    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:472:1: fromClause : ( KW_FROM joinSource -> ^( TOK_FROM joinSource ) | KW_FROM fromSource -> ^( TOK_FROM fromSource ) );
    public final fromClause_return fromClause() throws RecognitionException {
        fromClause_return retval = new fromClause_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token KW_FROM236=null;
        Token KW_FROM238=null;
        joinSource_return joinSource237 = null;

        fromSource_return fromSource239 = null;


        CommonTree KW_FROM236_tree=null;
        CommonTree KW_FROM238_tree=null;
        RewriteRuleTokenStream stream_KW_FROM=new RewriteRuleTokenStream(adaptor,"token KW_FROM");
        RewriteRuleSubtreeStream stream_fromSource=new RewriteRuleSubtreeStream(adaptor,"rule fromSource");
        RewriteRuleSubtreeStream stream_joinSource=new RewriteRuleSubtreeStream(adaptor,"rule joinSource");
        try {
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:473:5: ( KW_FROM joinSource -> ^( TOK_FROM joinSource ) | KW_FROM fromSource -> ^( TOK_FROM fromSource ) )
            int alt69=2;
            int LA69_0 = input.LA(1);

            if ( (LA69_0==KW_FROM) ) {
                int LA69_1 = input.LA(2);

                if ( (synpred88()) ) {
                    alt69=1;
                }
                else if ( (true) ) {
                    alt69=2;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("472:1: fromClause : ( KW_FROM joinSource -> ^( TOK_FROM joinSource ) | KW_FROM fromSource -> ^( TOK_FROM fromSource ) );", 69, 1, input);

                    throw nvae;
                }
            }
            else {
                if (backtracking>0) {failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("472:1: fromClause : ( KW_FROM joinSource -> ^( TOK_FROM joinSource ) | KW_FROM fromSource -> ^( TOK_FROM fromSource ) );", 69, 0, input);

                throw nvae;
            }
            switch (alt69) {
                case 1 :
                    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:474:7: KW_FROM joinSource
                    {
                    KW_FROM236=(Token)input.LT(1);
                    match(input,KW_FROM,FOLLOW_KW_FROM_in_fromClause3412); if (failed) return retval;
                    if ( backtracking==0 ) stream_KW_FROM.add(KW_FROM236);

                    pushFollow(FOLLOW_joinSource_in_fromClause3414);
                    joinSource237=joinSource();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_joinSource.add(joinSource237.getTree());

                    // AST REWRITE
                    // elements: joinSource
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    if ( backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 474:26: -> ^( TOK_FROM joinSource )
                    {
                        // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:474:29: ^( TOK_FROM joinSource )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot(adaptor.create(TOK_FROM, "TOK_FROM"), root_1);

                        adaptor.addChild(root_1, stream_joinSource.next());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    }

                    }
                    break;
                case 2 :
                    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:475:7: KW_FROM fromSource
                    {
                    KW_FROM238=(Token)input.LT(1);
                    match(input,KW_FROM,FOLLOW_KW_FROM_in_fromClause3430); if (failed) return retval;
                    if ( backtracking==0 ) stream_KW_FROM.add(KW_FROM238);

                    pushFollow(FOLLOW_fromSource_in_fromClause3432);
                    fromSource239=fromSource();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_fromSource.add(fromSource239.getTree());

                    // AST REWRITE
                    // elements: fromSource
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    if ( backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 475:26: -> ^( TOK_FROM fromSource )
                    {
                        // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:475:29: ^( TOK_FROM fromSource )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot(adaptor.create(TOK_FROM, "TOK_FROM"), root_1);

                        adaptor.addChild(root_1, stream_fromSource.next());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    }

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e) {
          reportError(e);
          throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end fromClause

    public static class joinSource_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start joinSource
    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:478:1: joinSource : fromSource ( joinToken fromSource ( KW_ON precedenceEqualExpression )? )+ ;
    public final joinSource_return joinSource() throws RecognitionException {
        joinSource_return retval = new joinSource_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token KW_ON243=null;
        fromSource_return fromSource240 = null;

        joinToken_return joinToken241 = null;

        fromSource_return fromSource242 = null;

        precedenceEqualExpression_return precedenceEqualExpression244 = null;


        CommonTree KW_ON243_tree=null;

        try {
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:479:5: ( fromSource ( joinToken fromSource ( KW_ON precedenceEqualExpression )? )+ )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:480:5: fromSource ( joinToken fromSource ( KW_ON precedenceEqualExpression )? )+
            {
            root_0 = (CommonTree)adaptor.nil();

            pushFollow(FOLLOW_fromSource_in_joinSource3465);
            fromSource240=fromSource();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) adaptor.addChild(root_0, fromSource240.getTree());
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:481:5: ( joinToken fromSource ( KW_ON precedenceEqualExpression )? )+
            int cnt71=0;
            loop71:
            do {
                int alt71=2;
                int LA71_0 = input.LA(1);

                if ( ((LA71_0>=KW_JOIN && LA71_0<=KW_LEFT)||(LA71_0>=KW_RIGHT && LA71_0<=KW_FULL)) ) {
                    alt71=1;
                }


                switch (alt71) {
            	case 1 :
            	    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:481:7: joinToken fromSource ( KW_ON precedenceEqualExpression )?
            	    {
            	    pushFollow(FOLLOW_joinToken_in_joinSource3474);
            	    joinToken241=joinToken();
            	    _fsp--;
            	    if (failed) return retval;
            	    if ( backtracking==0 ) root_0 = (CommonTree)adaptor.becomeRoot(joinToken241.getTree(), root_0);
            	    pushFollow(FOLLOW_fromSource_in_joinSource3477);
            	    fromSource242=fromSource();
            	    _fsp--;
            	    if (failed) return retval;
            	    if ( backtracking==0 ) adaptor.addChild(root_0, fromSource242.getTree());
            	    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:481:29: ( KW_ON precedenceEqualExpression )?
            	    int alt70=2;
            	    int LA70_0 = input.LA(1);

            	    if ( (LA70_0==KW_ON) ) {
            	        alt70=1;
            	    }
            	    switch (alt70) {
            	        case 1 :
            	            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:481:30: KW_ON precedenceEqualExpression
            	            {
            	            KW_ON243=(Token)input.LT(1);
            	            match(input,KW_ON,FOLLOW_KW_ON_in_joinSource3480); if (failed) return retval;
            	            pushFollow(FOLLOW_precedenceEqualExpression_in_joinSource3483);
            	            precedenceEqualExpression244=precedenceEqualExpression();
            	            _fsp--;
            	            if (failed) return retval;
            	            if ( backtracking==0 ) adaptor.addChild(root_0, precedenceEqualExpression244.getTree());

            	            }
            	            break;

            	    }


            	    }
            	    break;

            	default :
            	    if ( cnt71 >= 1 ) break loop71;
            	    if (backtracking>0) {failed=true; return retval;}
                        EarlyExitException eee =
                            new EarlyExitException(71, input);
                        throw eee;
                }
                cnt71++;
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e) {
          reportError(e);
          throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end joinSource

    public static class joinToken_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start joinToken
    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:484:1: joinToken : ( KW_JOIN -> TOK_JOIN | KW_LEFT KW_OUTER KW_JOIN -> TOK_LEFTOUTERJOIN | KW_RIGHT KW_OUTER KW_JOIN -> TOK_RIGHTOUTERJOIN | KW_FULL KW_OUTER KW_JOIN -> TOK_FULLOUTERJOIN );
    public final joinToken_return joinToken() throws RecognitionException {
        joinToken_return retval = new joinToken_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token KW_JOIN245=null;
        Token KW_LEFT246=null;
        Token KW_OUTER247=null;
        Token KW_JOIN248=null;
        Token KW_RIGHT249=null;
        Token KW_OUTER250=null;
        Token KW_JOIN251=null;
        Token KW_FULL252=null;
        Token KW_OUTER253=null;
        Token KW_JOIN254=null;

        CommonTree KW_JOIN245_tree=null;
        CommonTree KW_LEFT246_tree=null;
        CommonTree KW_OUTER247_tree=null;
        CommonTree KW_JOIN248_tree=null;
        CommonTree KW_RIGHT249_tree=null;
        CommonTree KW_OUTER250_tree=null;
        CommonTree KW_JOIN251_tree=null;
        CommonTree KW_FULL252_tree=null;
        CommonTree KW_OUTER253_tree=null;
        CommonTree KW_JOIN254_tree=null;
        RewriteRuleTokenStream stream_KW_RIGHT=new RewriteRuleTokenStream(adaptor,"token KW_RIGHT");
        RewriteRuleTokenStream stream_KW_OUTER=new RewriteRuleTokenStream(adaptor,"token KW_OUTER");
        RewriteRuleTokenStream stream_KW_JOIN=new RewriteRuleTokenStream(adaptor,"token KW_JOIN");
        RewriteRuleTokenStream stream_KW_LEFT=new RewriteRuleTokenStream(adaptor,"token KW_LEFT");
        RewriteRuleTokenStream stream_KW_FULL=new RewriteRuleTokenStream(adaptor,"token KW_FULL");

        try {
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:485:5: ( KW_JOIN -> TOK_JOIN | KW_LEFT KW_OUTER KW_JOIN -> TOK_LEFTOUTERJOIN | KW_RIGHT KW_OUTER KW_JOIN -> TOK_RIGHTOUTERJOIN | KW_FULL KW_OUTER KW_JOIN -> TOK_FULLOUTERJOIN )
            int alt72=4;
            switch ( input.LA(1) ) {
            case KW_JOIN:
                {
                alt72=1;
                }
                break;
            case KW_LEFT:
                {
                alt72=2;
                }
                break;
            case KW_RIGHT:
                {
                alt72=3;
                }
                break;
            case KW_FULL:
                {
                alt72=4;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("484:1: joinToken : ( KW_JOIN -> TOK_JOIN | KW_LEFT KW_OUTER KW_JOIN -> TOK_LEFTOUTERJOIN | KW_RIGHT KW_OUTER KW_JOIN -> TOK_RIGHTOUTERJOIN | KW_FULL KW_OUTER KW_JOIN -> TOK_FULLOUTERJOIN );", 72, 0, input);

                throw nvae;
            }

            switch (alt72) {
                case 1 :
                    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:486:7: KW_JOIN
                    {
                    KW_JOIN245=(Token)input.LT(1);
                    match(input,KW_JOIN,FOLLOW_KW_JOIN_in_joinToken3511); if (failed) return retval;
                    if ( backtracking==0 ) stream_KW_JOIN.add(KW_JOIN245);


                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    if ( backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 486:35: -> TOK_JOIN
                    {
                        adaptor.addChild(root_0, adaptor.create(TOK_JOIN, "TOK_JOIN"));

                    }

                    }

                    }
                    break;
                case 2 :
                    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:487:7: KW_LEFT KW_OUTER KW_JOIN
                    {
                    KW_LEFT246=(Token)input.LT(1);
                    match(input,KW_LEFT,FOLLOW_KW_LEFT_in_joinToken3543); if (failed) return retval;
                    if ( backtracking==0 ) stream_KW_LEFT.add(KW_LEFT246);

                    KW_OUTER247=(Token)input.LT(1);
                    match(input,KW_OUTER,FOLLOW_KW_OUTER_in_joinToken3545); if (failed) return retval;
                    if ( backtracking==0 ) stream_KW_OUTER.add(KW_OUTER247);

                    KW_JOIN248=(Token)input.LT(1);
                    match(input,KW_JOIN,FOLLOW_KW_JOIN_in_joinToken3547); if (failed) return retval;
                    if ( backtracking==0 ) stream_KW_JOIN.add(KW_JOIN248);


                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    if ( backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 487:35: -> TOK_LEFTOUTERJOIN
                    {
                        adaptor.addChild(root_0, adaptor.create(TOK_LEFTOUTERJOIN, "TOK_LEFTOUTERJOIN"));

                    }

                    }

                    }
                    break;
                case 3 :
                    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:488:7: KW_RIGHT KW_OUTER KW_JOIN
                    {
                    KW_RIGHT249=(Token)input.LT(1);
                    match(input,KW_RIGHT,FOLLOW_KW_RIGHT_in_joinToken3562); if (failed) return retval;
                    if ( backtracking==0 ) stream_KW_RIGHT.add(KW_RIGHT249);

                    KW_OUTER250=(Token)input.LT(1);
                    match(input,KW_OUTER,FOLLOW_KW_OUTER_in_joinToken3564); if (failed) return retval;
                    if ( backtracking==0 ) stream_KW_OUTER.add(KW_OUTER250);

                    KW_JOIN251=(Token)input.LT(1);
                    match(input,KW_JOIN,FOLLOW_KW_JOIN_in_joinToken3566); if (failed) return retval;
                    if ( backtracking==0 ) stream_KW_JOIN.add(KW_JOIN251);


                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    if ( backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 488:35: -> TOK_RIGHTOUTERJOIN
                    {
                        adaptor.addChild(root_0, adaptor.create(TOK_RIGHTOUTERJOIN, "TOK_RIGHTOUTERJOIN"));

                    }

                    }

                    }
                    break;
                case 4 :
                    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:489:7: KW_FULL KW_OUTER KW_JOIN
                    {
                    KW_FULL252=(Token)input.LT(1);
                    match(input,KW_FULL,FOLLOW_KW_FULL_in_joinToken3580); if (failed) return retval;
                    if ( backtracking==0 ) stream_KW_FULL.add(KW_FULL252);

                    KW_OUTER253=(Token)input.LT(1);
                    match(input,KW_OUTER,FOLLOW_KW_OUTER_in_joinToken3582); if (failed) return retval;
                    if ( backtracking==0 ) stream_KW_OUTER.add(KW_OUTER253);

                    KW_JOIN254=(Token)input.LT(1);
                    match(input,KW_JOIN,FOLLOW_KW_JOIN_in_joinToken3584); if (failed) return retval;
                    if ( backtracking==0 ) stream_KW_JOIN.add(KW_JOIN254);


                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    if ( backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 489:35: -> TOK_FULLOUTERJOIN
                    {
                        adaptor.addChild(root_0, adaptor.create(TOK_FULLOUTERJOIN, "TOK_FULLOUTERJOIN"));

                    }

                    }

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e) {
          reportError(e);
          throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end joinToken

    public static class fromSource_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start fromSource
    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:492:1: fromSource : ( tableSource | subQuerySource ) ;
    public final fromSource_return fromSource() throws RecognitionException {
        fromSource_return retval = new fromSource_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        tableSource_return tableSource255 = null;

        subQuerySource_return subQuerySource256 = null;



        try {
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:493:5: ( ( tableSource | subQuerySource ) )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:494:5: ( tableSource | subQuerySource )
            {
            root_0 = (CommonTree)adaptor.nil();

            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:494:5: ( tableSource | subQuerySource )
            int alt73=2;
            int LA73_0 = input.LA(1);

            if ( (LA73_0==Identifier) ) {
                alt73=1;
            }
            else if ( (LA73_0==LPAREN) ) {
                alt73=2;
            }
            else {
                if (backtracking>0) {failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("494:5: ( tableSource | subQuerySource )", 73, 0, input);

                throw nvae;
            }
            switch (alt73) {
                case 1 :
                    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:494:6: tableSource
                    {
                    pushFollow(FOLLOW_tableSource_in_fromSource3613);
                    tableSource255=tableSource();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, tableSource255.getTree());

                    }
                    break;
                case 2 :
                    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:494:20: subQuerySource
                    {
                    pushFollow(FOLLOW_subQuerySource_in_fromSource3617);
                    subQuerySource256=subQuerySource();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, subQuerySource256.getTree());

                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e) {
          reportError(e);
          throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end fromSource

    public static class tableSample_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start tableSample
    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:497:1: tableSample : KW_TABLESAMPLE LPAREN KW_BUCKET (numerator= Number ) KW_OUT KW_OF (denominator= Number ) ( KW_ON col+= Identifier ( COMMA col+= Identifier )* )? RPAREN -> ^( TOK_TABLESAMPLE $numerator $denominator ( $col)* ) ;
    public final tableSample_return tableSample() throws RecognitionException {
        tableSample_return retval = new tableSample_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token numerator=null;
        Token denominator=null;
        Token KW_TABLESAMPLE257=null;
        Token LPAREN258=null;
        Token KW_BUCKET259=null;
        Token KW_OUT260=null;
        Token KW_OF261=null;
        Token KW_ON262=null;
        Token COMMA263=null;
        Token RPAREN264=null;
        Token col=null;
        List list_col=null;

        CommonTree numerator_tree=null;
        CommonTree denominator_tree=null;
        CommonTree KW_TABLESAMPLE257_tree=null;
        CommonTree LPAREN258_tree=null;
        CommonTree KW_BUCKET259_tree=null;
        CommonTree KW_OUT260_tree=null;
        CommonTree KW_OF261_tree=null;
        CommonTree KW_ON262_tree=null;
        CommonTree COMMA263_tree=null;
        CommonTree RPAREN264_tree=null;
        CommonTree col_tree=null;
        RewriteRuleTokenStream stream_RPAREN=new RewriteRuleTokenStream(adaptor,"token RPAREN");
        RewriteRuleTokenStream stream_KW_OUT=new RewriteRuleTokenStream(adaptor,"token KW_OUT");
        RewriteRuleTokenStream stream_KW_OF=new RewriteRuleTokenStream(adaptor,"token KW_OF");
        RewriteRuleTokenStream stream_Number=new RewriteRuleTokenStream(adaptor,"token Number");
        RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
        RewriteRuleTokenStream stream_KW_BUCKET=new RewriteRuleTokenStream(adaptor,"token KW_BUCKET");
        RewriteRuleTokenStream stream_Identifier=new RewriteRuleTokenStream(adaptor,"token Identifier");
        RewriteRuleTokenStream stream_KW_TABLESAMPLE=new RewriteRuleTokenStream(adaptor,"token KW_TABLESAMPLE");
        RewriteRuleTokenStream stream_LPAREN=new RewriteRuleTokenStream(adaptor,"token LPAREN");
        RewriteRuleTokenStream stream_KW_ON=new RewriteRuleTokenStream(adaptor,"token KW_ON");

        try {
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:498:5: ( KW_TABLESAMPLE LPAREN KW_BUCKET (numerator= Number ) KW_OUT KW_OF (denominator= Number ) ( KW_ON col+= Identifier ( COMMA col+= Identifier )* )? RPAREN -> ^( TOK_TABLESAMPLE $numerator $denominator ( $col)* ) )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:499:5: KW_TABLESAMPLE LPAREN KW_BUCKET (numerator= Number ) KW_OUT KW_OF (denominator= Number ) ( KW_ON col+= Identifier ( COMMA col+= Identifier )* )? RPAREN
            {
            KW_TABLESAMPLE257=(Token)input.LT(1);
            match(input,KW_TABLESAMPLE,FOLLOW_KW_TABLESAMPLE_in_tableSample3643); if (failed) return retval;
            if ( backtracking==0 ) stream_KW_TABLESAMPLE.add(KW_TABLESAMPLE257);

            LPAREN258=(Token)input.LT(1);
            match(input,LPAREN,FOLLOW_LPAREN_in_tableSample3645); if (failed) return retval;
            if ( backtracking==0 ) stream_LPAREN.add(LPAREN258);

            KW_BUCKET259=(Token)input.LT(1);
            match(input,KW_BUCKET,FOLLOW_KW_BUCKET_in_tableSample3647); if (failed) return retval;
            if ( backtracking==0 ) stream_KW_BUCKET.add(KW_BUCKET259);

            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:499:37: (numerator= Number )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:499:38: numerator= Number
            {
            numerator=(Token)input.LT(1);
            match(input,Number,FOLLOW_Number_in_tableSample3652); if (failed) return retval;
            if ( backtracking==0 ) stream_Number.add(numerator);


            }

            KW_OUT260=(Token)input.LT(1);
            match(input,KW_OUT,FOLLOW_KW_OUT_in_tableSample3655); if (failed) return retval;
            if ( backtracking==0 ) stream_KW_OUT.add(KW_OUT260);

            KW_OF261=(Token)input.LT(1);
            match(input,KW_OF,FOLLOW_KW_OF_in_tableSample3657); if (failed) return retval;
            if ( backtracking==0 ) stream_KW_OF.add(KW_OF261);

            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:499:69: (denominator= Number )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:499:70: denominator= Number
            {
            denominator=(Token)input.LT(1);
            match(input,Number,FOLLOW_Number_in_tableSample3662); if (failed) return retval;
            if ( backtracking==0 ) stream_Number.add(denominator);


            }

            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:499:90: ( KW_ON col+= Identifier ( COMMA col+= Identifier )* )?
            int alt75=2;
            int LA75_0 = input.LA(1);

            if ( (LA75_0==KW_ON) ) {
                alt75=1;
            }
            switch (alt75) {
                case 1 :
                    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:499:91: KW_ON col+= Identifier ( COMMA col+= Identifier )*
                    {
                    KW_ON262=(Token)input.LT(1);
                    match(input,KW_ON,FOLLOW_KW_ON_in_tableSample3666); if (failed) return retval;
                    if ( backtracking==0 ) stream_KW_ON.add(KW_ON262);

                    col=(Token)input.LT(1);
                    match(input,Identifier,FOLLOW_Identifier_in_tableSample3670); if (failed) return retval;
                    if ( backtracking==0 ) stream_Identifier.add(col);

                    if (list_col==null) list_col=new ArrayList();
                    list_col.add(col);

                    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:499:113: ( COMMA col+= Identifier )*
                    loop74:
                    do {
                        int alt74=2;
                        int LA74_0 = input.LA(1);

                        if ( (LA74_0==COMMA) ) {
                            alt74=1;
                        }


                        switch (alt74) {
                    	case 1 :
                    	    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:499:114: COMMA col+= Identifier
                    	    {
                    	    COMMA263=(Token)input.LT(1);
                    	    match(input,COMMA,FOLLOW_COMMA_in_tableSample3673); if (failed) return retval;
                    	    if ( backtracking==0 ) stream_COMMA.add(COMMA263);

                    	    col=(Token)input.LT(1);
                    	    match(input,Identifier,FOLLOW_Identifier_in_tableSample3677); if (failed) return retval;
                    	    if ( backtracking==0 ) stream_Identifier.add(col);

                    	    if (list_col==null) list_col=new ArrayList();
                    	    list_col.add(col);


                    	    }
                    	    break;

                    	default :
                    	    break loop74;
                        }
                    } while (true);


                    }
                    break;

            }

            RPAREN264=(Token)input.LT(1);
            match(input,RPAREN,FOLLOW_RPAREN_in_tableSample3683); if (failed) return retval;
            if ( backtracking==0 ) stream_RPAREN.add(RPAREN264);


            // AST REWRITE
            // elements: col, numerator, denominator
            // token labels: denominator, numerator
            // rule labels: retval
            // token list labels: col
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleTokenStream stream_denominator=new RewriteRuleTokenStream(adaptor,"token denominator",denominator);
            RewriteRuleTokenStream stream_numerator=new RewriteRuleTokenStream(adaptor,"token numerator",numerator);
            RewriteRuleTokenStream stream_col=new RewriteRuleTokenStream(adaptor,"token col", list_col);
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 499:147: -> ^( TOK_TABLESAMPLE $numerator $denominator ( $col)* )
            {
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:499:150: ^( TOK_TABLESAMPLE $numerator $denominator ( $col)* )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(adaptor.create(TOK_TABLESAMPLE, "TOK_TABLESAMPLE"), root_1);

                adaptor.addChild(root_1, stream_numerator.next());
                adaptor.addChild(root_1, stream_denominator.next());
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:499:192: ( $col)*
                while ( stream_col.hasNext() ) {
                    adaptor.addChild(root_1, stream_col.next());

                }
                stream_col.reset();

                adaptor.addChild(root_0, root_1);
                }

            }

            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e) {
          reportError(e);
          throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end tableSample

    public static class tableSource_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start tableSource
    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:502:1: tableSource : tabname= Identifier (ts= tableSample )? (alias= Identifier )? -> ^( TOK_TABREF $tabname ( $ts)? ( $alias)? ) ;
    public final tableSource_return tableSource() throws RecognitionException {
        tableSource_return retval = new tableSource_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token tabname=null;
        Token alias=null;
        tableSample_return ts = null;


        CommonTree tabname_tree=null;
        CommonTree alias_tree=null;
        RewriteRuleTokenStream stream_Identifier=new RewriteRuleTokenStream(adaptor,"token Identifier");
        RewriteRuleSubtreeStream stream_tableSample=new RewriteRuleSubtreeStream(adaptor,"rule tableSample");
        try {
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:503:5: (tabname= Identifier (ts= tableSample )? (alias= Identifier )? -> ^( TOK_TABREF $tabname ( $ts)? ( $alias)? ) )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:504:5: tabname= Identifier (ts= tableSample )? (alias= Identifier )?
            {
            tabname=(Token)input.LT(1);
            match(input,Identifier,FOLLOW_Identifier_in_tableSource3722); if (failed) return retval;
            if ( backtracking==0 ) stream_Identifier.add(tabname);

            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:504:24: (ts= tableSample )?
            int alt76=2;
            int LA76_0 = input.LA(1);

            if ( (LA76_0==KW_TABLESAMPLE) ) {
                alt76=1;
            }
            switch (alt76) {
                case 1 :
                    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:504:25: ts= tableSample
                    {
                    pushFollow(FOLLOW_tableSample_in_tableSource3727);
                    ts=tableSample();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_tableSample.add(ts.getTree());

                    }
                    break;

            }

            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:504:42: (alias= Identifier )?
            int alt77=2;
            int LA77_0 = input.LA(1);

            if ( (LA77_0==Identifier) ) {
                alt77=1;
            }
            switch (alt77) {
                case 1 :
                    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:504:43: alias= Identifier
                    {
                    alias=(Token)input.LT(1);
                    match(input,Identifier,FOLLOW_Identifier_in_tableSource3734); if (failed) return retval;
                    if ( backtracking==0 ) stream_Identifier.add(alias);


                    }
                    break;

            }


            // AST REWRITE
            // elements: tabname, ts, alias
            // token labels: alias, tabname
            // rule labels: retval, ts
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleTokenStream stream_alias=new RewriteRuleTokenStream(adaptor,"token alias",alias);
            RewriteRuleTokenStream stream_tabname=new RewriteRuleTokenStream(adaptor,"token tabname",tabname);
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);
            RewriteRuleSubtreeStream stream_ts=new RewriteRuleSubtreeStream(adaptor,"token ts",ts!=null?ts.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 504:62: -> ^( TOK_TABREF $tabname ( $ts)? ( $alias)? )
            {
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:504:65: ^( TOK_TABREF $tabname ( $ts)? ( $alias)? )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(adaptor.create(TOK_TABREF, "TOK_TABREF"), root_1);

                adaptor.addChild(root_1, stream_tabname.next());
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:504:87: ( $ts)?
                if ( stream_ts.hasNext() ) {
                    adaptor.addChild(root_1, stream_ts.next());

                }
                stream_ts.reset();
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:504:92: ( $alias)?
                if ( stream_alias.hasNext() ) {
                    adaptor.addChild(root_1, stream_alias.next());

                }
                stream_alias.reset();

                adaptor.addChild(root_0, root_1);
                }

            }

            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e) {
          reportError(e);
          throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end tableSource

    public static class subQuerySource_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start subQuerySource
    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:508:1: subQuerySource : LPAREN queryStatementExpression RPAREN Identifier -> ^( TOK_SUBQUERY queryStatementExpression Identifier ) ;
    public final subQuerySource_return subQuerySource() throws RecognitionException {
        subQuerySource_return retval = new subQuerySource_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token LPAREN265=null;
        Token RPAREN267=null;
        Token Identifier268=null;
        queryStatementExpression_return queryStatementExpression266 = null;


        CommonTree LPAREN265_tree=null;
        CommonTree RPAREN267_tree=null;
        CommonTree Identifier268_tree=null;
        RewriteRuleTokenStream stream_RPAREN=new RewriteRuleTokenStream(adaptor,"token RPAREN");
        RewriteRuleTokenStream stream_Identifier=new RewriteRuleTokenStream(adaptor,"token Identifier");
        RewriteRuleTokenStream stream_LPAREN=new RewriteRuleTokenStream(adaptor,"token LPAREN");
        RewriteRuleSubtreeStream stream_queryStatementExpression=new RewriteRuleSubtreeStream(adaptor,"rule queryStatementExpression");
        try {
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:509:5: ( LPAREN queryStatementExpression RPAREN Identifier -> ^( TOK_SUBQUERY queryStatementExpression Identifier ) )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:510:5: LPAREN queryStatementExpression RPAREN Identifier
            {
            LPAREN265=(Token)input.LT(1);
            match(input,LPAREN,FOLLOW_LPAREN_in_subQuerySource3776); if (failed) return retval;
            if ( backtracking==0 ) stream_LPAREN.add(LPAREN265);

            pushFollow(FOLLOW_queryStatementExpression_in_subQuerySource3778);
            queryStatementExpression266=queryStatementExpression();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_queryStatementExpression.add(queryStatementExpression266.getTree());
            RPAREN267=(Token)input.LT(1);
            match(input,RPAREN,FOLLOW_RPAREN_in_subQuerySource3780); if (failed) return retval;
            if ( backtracking==0 ) stream_RPAREN.add(RPAREN267);

            Identifier268=(Token)input.LT(1);
            match(input,Identifier,FOLLOW_Identifier_in_subQuerySource3782); if (failed) return retval;
            if ( backtracking==0 ) stream_Identifier.add(Identifier268);


            // AST REWRITE
            // elements: queryStatementExpression, Identifier
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 510:55: -> ^( TOK_SUBQUERY queryStatementExpression Identifier )
            {
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:510:58: ^( TOK_SUBQUERY queryStatementExpression Identifier )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(adaptor.create(TOK_SUBQUERY, "TOK_SUBQUERY"), root_1);

                adaptor.addChild(root_1, stream_queryStatementExpression.next());
                adaptor.addChild(root_1, stream_Identifier.next());

                adaptor.addChild(root_0, root_1);
                }

            }

            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e) {
          reportError(e);
          throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end subQuerySource

    public static class whereClause_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start whereClause
    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:515:1: whereClause : KW_WHERE searchCondition -> ^( TOK_WHERE searchCondition ) ;
    public final whereClause_return whereClause() throws RecognitionException {
        whereClause_return retval = new whereClause_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token KW_WHERE269=null;
        searchCondition_return searchCondition270 = null;


        CommonTree KW_WHERE269_tree=null;
        RewriteRuleTokenStream stream_KW_WHERE=new RewriteRuleTokenStream(adaptor,"token KW_WHERE");
        RewriteRuleSubtreeStream stream_searchCondition=new RewriteRuleSubtreeStream(adaptor,"rule searchCondition");
        try {
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:516:5: ( KW_WHERE searchCondition -> ^( TOK_WHERE searchCondition ) )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:517:5: KW_WHERE searchCondition
            {
            KW_WHERE269=(Token)input.LT(1);
            match(input,KW_WHERE,FOLLOW_KW_WHERE_in_whereClause3823); if (failed) return retval;
            if ( backtracking==0 ) stream_KW_WHERE.add(KW_WHERE269);

            pushFollow(FOLLOW_searchCondition_in_whereClause3825);
            searchCondition270=searchCondition();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_searchCondition.add(searchCondition270.getTree());

            // AST REWRITE
            // elements: searchCondition
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 517:30: -> ^( TOK_WHERE searchCondition )
            {
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:517:33: ^( TOK_WHERE searchCondition )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(adaptor.create(TOK_WHERE, "TOK_WHERE"), root_1);

                adaptor.addChild(root_1, stream_searchCondition.next());

                adaptor.addChild(root_0, root_1);
                }

            }

            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e) {
          reportError(e);
          throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end whereClause

    public static class searchCondition_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start searchCondition
    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:520:1: searchCondition : expression ;
    public final searchCondition_return searchCondition() throws RecognitionException {
        searchCondition_return retval = new searchCondition_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        expression_return expression271 = null;



        try {
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:521:5: ( expression )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:522:5: expression
            {
            root_0 = (CommonTree)adaptor.nil();

            pushFollow(FOLLOW_expression_in_searchCondition3854);
            expression271=expression();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) adaptor.addChild(root_0, expression271.getTree());

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e) {
          reportError(e);
          throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end searchCondition

    public static class groupByClause_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start groupByClause
    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:528:1: groupByClause : KW_GROUP KW_BY groupByExpression ( COMMA groupByExpression )* -> ^( TOK_GROUPBY ( groupByExpression )+ ) ;
    public final groupByClause_return groupByClause() throws RecognitionException {
        groupByClause_return retval = new groupByClause_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token KW_GROUP272=null;
        Token KW_BY273=null;
        Token COMMA275=null;
        groupByExpression_return groupByExpression274 = null;

        groupByExpression_return groupByExpression276 = null;


        CommonTree KW_GROUP272_tree=null;
        CommonTree KW_BY273_tree=null;
        CommonTree COMMA275_tree=null;
        RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
        RewriteRuleTokenStream stream_KW_GROUP=new RewriteRuleTokenStream(adaptor,"token KW_GROUP");
        RewriteRuleTokenStream stream_KW_BY=new RewriteRuleTokenStream(adaptor,"token KW_BY");
        RewriteRuleSubtreeStream stream_groupByExpression=new RewriteRuleSubtreeStream(adaptor,"rule groupByExpression");
        try {
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:529:5: ( KW_GROUP KW_BY groupByExpression ( COMMA groupByExpression )* -> ^( TOK_GROUPBY ( groupByExpression )+ ) )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:530:5: KW_GROUP KW_BY groupByExpression ( COMMA groupByExpression )*
            {
            KW_GROUP272=(Token)input.LT(1);
            match(input,KW_GROUP,FOLLOW_KW_GROUP_in_groupByClause3878); if (failed) return retval;
            if ( backtracking==0 ) stream_KW_GROUP.add(KW_GROUP272);

            KW_BY273=(Token)input.LT(1);
            match(input,KW_BY,FOLLOW_KW_BY_in_groupByClause3880); if (failed) return retval;
            if ( backtracking==0 ) stream_KW_BY.add(KW_BY273);

            pushFollow(FOLLOW_groupByExpression_in_groupByClause3886);
            groupByExpression274=groupByExpression();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_groupByExpression.add(groupByExpression274.getTree());
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:532:5: ( COMMA groupByExpression )*
            loop78:
            do {
                int alt78=2;
                int LA78_0 = input.LA(1);

                if ( (LA78_0==COMMA) ) {
                    alt78=1;
                }


                switch (alt78) {
            	case 1 :
            	    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:532:7: COMMA groupByExpression
            	    {
            	    COMMA275=(Token)input.LT(1);
            	    match(input,COMMA,FOLLOW_COMMA_in_groupByClause3894); if (failed) return retval;
            	    if ( backtracking==0 ) stream_COMMA.add(COMMA275);

            	    pushFollow(FOLLOW_groupByExpression_in_groupByClause3896);
            	    groupByExpression276=groupByExpression();
            	    _fsp--;
            	    if (failed) return retval;
            	    if ( backtracking==0 ) stream_groupByExpression.add(groupByExpression276.getTree());

            	    }
            	    break;

            	default :
            	    break loop78;
                }
            } while (true);


            // AST REWRITE
            // elements: groupByExpression
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 533:5: -> ^( TOK_GROUPBY ( groupByExpression )+ )
            {
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:533:8: ^( TOK_GROUPBY ( groupByExpression )+ )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(adaptor.create(TOK_GROUPBY, "TOK_GROUPBY"), root_1);

                if ( !(stream_groupByExpression.hasNext()) ) {
                    throw new RewriteEarlyExitException();
                }
                while ( stream_groupByExpression.hasNext() ) {
                    adaptor.addChild(root_1, stream_groupByExpression.next());

                }
                stream_groupByExpression.reset();

                adaptor.addChild(root_0, root_1);
                }

            }

            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e) {
          reportError(e);
          throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end groupByClause

    public static class groupByExpression_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start groupByExpression
    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:536:1: groupByExpression : expression ;
    public final groupByExpression_return groupByExpression() throws RecognitionException {
        groupByExpression_return retval = new groupByExpression_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        expression_return expression277 = null;



        try {
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:537:5: ( expression )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:538:5: expression
            {
            root_0 = (CommonTree)adaptor.nil();

            pushFollow(FOLLOW_expression_in_groupByExpression3933);
            expression277=expression();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) adaptor.addChild(root_0, expression277.getTree());

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e) {
          reportError(e);
          throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end groupByExpression

    public static class orderByClause_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start orderByClause
    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:542:1: orderByClause : KW_ORDER KW_BY orderByExpression ( COMMA orderByExpression )* -> ^( TOK_ORDERBY ( orderByExpression )+ ) ;
    public final orderByClause_return orderByClause() throws RecognitionException {
        orderByClause_return retval = new orderByClause_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token KW_ORDER278=null;
        Token KW_BY279=null;
        Token COMMA281=null;
        orderByExpression_return orderByExpression280 = null;

        orderByExpression_return orderByExpression282 = null;


        CommonTree KW_ORDER278_tree=null;
        CommonTree KW_BY279_tree=null;
        CommonTree COMMA281_tree=null;
        RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
        RewriteRuleTokenStream stream_KW_ORDER=new RewriteRuleTokenStream(adaptor,"token KW_ORDER");
        RewriteRuleTokenStream stream_KW_BY=new RewriteRuleTokenStream(adaptor,"token KW_BY");
        RewriteRuleSubtreeStream stream_orderByExpression=new RewriteRuleSubtreeStream(adaptor,"rule orderByExpression");
        try {
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:543:5: ( KW_ORDER KW_BY orderByExpression ( COMMA orderByExpression )* -> ^( TOK_ORDERBY ( orderByExpression )+ ) )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:544:5: KW_ORDER KW_BY orderByExpression ( COMMA orderByExpression )*
            {
            KW_ORDER278=(Token)input.LT(1);
            match(input,KW_ORDER,FOLLOW_KW_ORDER_in_orderByClause3955); if (failed) return retval;
            if ( backtracking==0 ) stream_KW_ORDER.add(KW_ORDER278);

            KW_BY279=(Token)input.LT(1);
            match(input,KW_BY,FOLLOW_KW_BY_in_orderByClause3957); if (failed) return retval;
            if ( backtracking==0 ) stream_KW_BY.add(KW_BY279);

            pushFollow(FOLLOW_orderByExpression_in_orderByClause3963);
            orderByExpression280=orderByExpression();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_orderByExpression.add(orderByExpression280.getTree());
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:546:5: ( COMMA orderByExpression )*
            loop79:
            do {
                int alt79=2;
                int LA79_0 = input.LA(1);

                if ( (LA79_0==COMMA) ) {
                    alt79=1;
                }


                switch (alt79) {
            	case 1 :
            	    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:546:7: COMMA orderByExpression
            	    {
            	    COMMA281=(Token)input.LT(1);
            	    match(input,COMMA,FOLLOW_COMMA_in_orderByClause3971); if (failed) return retval;
            	    if ( backtracking==0 ) stream_COMMA.add(COMMA281);

            	    pushFollow(FOLLOW_orderByExpression_in_orderByClause3973);
            	    orderByExpression282=orderByExpression();
            	    _fsp--;
            	    if (failed) return retval;
            	    if ( backtracking==0 ) stream_orderByExpression.add(orderByExpression282.getTree());

            	    }
            	    break;

            	default :
            	    break loop79;
                }
            } while (true);


            // AST REWRITE
            // elements: orderByExpression
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 547:5: -> ^( TOK_ORDERBY ( orderByExpression )+ )
            {
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:547:8: ^( TOK_ORDERBY ( orderByExpression )+ )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(adaptor.create(TOK_ORDERBY, "TOK_ORDERBY"), root_1);

                if ( !(stream_orderByExpression.hasNext()) ) {
                    throw new RewriteEarlyExitException();
                }
                while ( stream_orderByExpression.hasNext() ) {
                    adaptor.addChild(root_1, stream_orderByExpression.next());

                }
                stream_orderByExpression.reset();

                adaptor.addChild(root_0, root_1);
                }

            }

            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e) {
          reportError(e);
          throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end orderByClause

    public static class orderByExpression_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start orderByExpression
    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:550:1: orderByExpression : expression ( KW_ASC | KW_DESC )? ;
    public final orderByExpression_return orderByExpression() throws RecognitionException {
        orderByExpression_return retval = new orderByExpression_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token set284=null;
        expression_return expression283 = null;


        CommonTree set284_tree=null;

        try {
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:551:5: ( expression ( KW_ASC | KW_DESC )? )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:552:5: expression ( KW_ASC | KW_DESC )?
            {
            root_0 = (CommonTree)adaptor.nil();

            pushFollow(FOLLOW_expression_in_orderByExpression4010);
            expression283=expression();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) adaptor.addChild(root_0, expression283.getTree());
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:553:5: ( KW_ASC | KW_DESC )?
            int alt80=2;
            int LA80_0 = input.LA(1);

            if ( ((LA80_0>=KW_ASC && LA80_0<=KW_DESC)) ) {
                alt80=1;
            }
            switch (alt80) {
                case 1 :
                    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:
                    {
                    set284=(Token)input.LT(1);
                    if ( (input.LA(1)>=KW_ASC && input.LA(1)<=KW_DESC) ) {
                        input.consume();
                        if ( backtracking==0 ) adaptor.addChild(root_0, adaptor.create(set284));
                        errorRecovery=false;failed=false;
                    }
                    else {
                        if (backtracking>0) {failed=true; return retval;}
                        MismatchedSetException mse =
                            new MismatchedSetException(null,input);
                        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_orderByExpression4016);    throw mse;
                    }


                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e) {
          reportError(e);
          throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end orderByExpression

    public static class clusterByClause_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start clusterByClause
    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:556:1: clusterByClause : KW_CLUSTER KW_BY Identifier ( COMMA Identifier )* -> ^( TOK_CLUSTERBY ( Identifier )+ ) ;
    public final clusterByClause_return clusterByClause() throws RecognitionException {
        clusterByClause_return retval = new clusterByClause_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token KW_CLUSTER285=null;
        Token KW_BY286=null;
        Token Identifier287=null;
        Token COMMA288=null;
        Token Identifier289=null;

        CommonTree KW_CLUSTER285_tree=null;
        CommonTree KW_BY286_tree=null;
        CommonTree Identifier287_tree=null;
        CommonTree COMMA288_tree=null;
        CommonTree Identifier289_tree=null;
        RewriteRuleTokenStream stream_KW_CLUSTER=new RewriteRuleTokenStream(adaptor,"token KW_CLUSTER");
        RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
        RewriteRuleTokenStream stream_Identifier=new RewriteRuleTokenStream(adaptor,"token Identifier");
        RewriteRuleTokenStream stream_KW_BY=new RewriteRuleTokenStream(adaptor,"token KW_BY");

        try {
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:557:5: ( KW_CLUSTER KW_BY Identifier ( COMMA Identifier )* -> ^( TOK_CLUSTERBY ( Identifier )+ ) )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:558:5: KW_CLUSTER KW_BY Identifier ( COMMA Identifier )*
            {
            KW_CLUSTER285=(Token)input.LT(1);
            match(input,KW_CLUSTER,FOLLOW_KW_CLUSTER_in_clusterByClause4044); if (failed) return retval;
            if ( backtracking==0 ) stream_KW_CLUSTER.add(KW_CLUSTER285);

            KW_BY286=(Token)input.LT(1);
            match(input,KW_BY,FOLLOW_KW_BY_in_clusterByClause4046); if (failed) return retval;
            if ( backtracking==0 ) stream_KW_BY.add(KW_BY286);

            Identifier287=(Token)input.LT(1);
            match(input,Identifier,FOLLOW_Identifier_in_clusterByClause4053); if (failed) return retval;
            if ( backtracking==0 ) stream_Identifier.add(Identifier287);

            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:560:5: ( COMMA Identifier )*
            loop81:
            do {
                int alt81=2;
                int LA81_0 = input.LA(1);

                if ( (LA81_0==COMMA) ) {
                    alt81=1;
                }


                switch (alt81) {
            	case 1 :
            	    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:560:7: COMMA Identifier
            	    {
            	    COMMA288=(Token)input.LT(1);
            	    match(input,COMMA,FOLLOW_COMMA_in_clusterByClause4062); if (failed) return retval;
            	    if ( backtracking==0 ) stream_COMMA.add(COMMA288);

            	    Identifier289=(Token)input.LT(1);
            	    match(input,Identifier,FOLLOW_Identifier_in_clusterByClause4064); if (failed) return retval;
            	    if ( backtracking==0 ) stream_Identifier.add(Identifier289);


            	    }
            	    break;

            	default :
            	    break loop81;
                }
            } while (true);


            // AST REWRITE
            // elements: Identifier
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 560:27: -> ^( TOK_CLUSTERBY ( Identifier )+ )
            {
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:560:30: ^( TOK_CLUSTERBY ( Identifier )+ )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(adaptor.create(TOK_CLUSTERBY, "TOK_CLUSTERBY"), root_1);

                if ( !(stream_Identifier.hasNext()) ) {
                    throw new RewriteEarlyExitException();
                }
                while ( stream_Identifier.hasNext() ) {
                    adaptor.addChild(root_1, stream_Identifier.next());

                }
                stream_Identifier.reset();

                adaptor.addChild(root_0, root_1);
                }

            }

            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e) {
          reportError(e);
          throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end clusterByClause

    public static class clusterByExpression_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start clusterByExpression
    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:563:1: clusterByExpression : expression ;
    public final clusterByExpression_return clusterByExpression() throws RecognitionException {
        clusterByExpression_return retval = new clusterByExpression_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        expression_return expression290 = null;



        try {
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:564:5: ( expression )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:565:5: expression
            {
            root_0 = (CommonTree)adaptor.nil();

            pushFollow(FOLLOW_expression_in_clusterByExpression4097);
            expression290=expression();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) adaptor.addChild(root_0, expression290.getTree());

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e) {
          reportError(e);
          throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end clusterByExpression

    public static class function_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start function
    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:569:1: function : Identifier LPAREN ( (dist= KW_DISTINCT )? expression ( COMMA expression )* )? RPAREN -> {$dist == null}? ^( TOK_FUNCTION Identifier ( expression )+ ) -> ^( TOK_FUNCTIONDI Identifier ( expression )+ ) ;
    public final function_return function() throws RecognitionException {
        function_return retval = new function_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token dist=null;
        Token Identifier291=null;
        Token LPAREN292=null;
        Token COMMA294=null;
        Token RPAREN296=null;
        expression_return expression293 = null;

        expression_return expression295 = null;


        CommonTree dist_tree=null;
        CommonTree Identifier291_tree=null;
        CommonTree LPAREN292_tree=null;
        CommonTree COMMA294_tree=null;
        CommonTree RPAREN296_tree=null;
        RewriteRuleTokenStream stream_RPAREN=new RewriteRuleTokenStream(adaptor,"token RPAREN");
        RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
        RewriteRuleTokenStream stream_Identifier=new RewriteRuleTokenStream(adaptor,"token Identifier");
        RewriteRuleTokenStream stream_KW_DISTINCT=new RewriteRuleTokenStream(adaptor,"token KW_DISTINCT");
        RewriteRuleTokenStream stream_LPAREN=new RewriteRuleTokenStream(adaptor,"token LPAREN");
        RewriteRuleSubtreeStream stream_expression=new RewriteRuleSubtreeStream(adaptor,"rule expression");
        try {
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:570:5: ( Identifier LPAREN ( (dist= KW_DISTINCT )? expression ( COMMA expression )* )? RPAREN -> {$dist == null}? ^( TOK_FUNCTION Identifier ( expression )+ ) -> ^( TOK_FUNCTIONDI Identifier ( expression )+ ) )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:571:5: Identifier LPAREN ( (dist= KW_DISTINCT )? expression ( COMMA expression )* )? RPAREN
            {
            Identifier291=(Token)input.LT(1);
            match(input,Identifier,FOLLOW_Identifier_in_function4121); if (failed) return retval;
            if ( backtracking==0 ) stream_Identifier.add(Identifier291);

            LPAREN292=(Token)input.LT(1);
            match(input,LPAREN,FOLLOW_LPAREN_in_function4127); if (failed) return retval;
            if ( backtracking==0 ) stream_LPAREN.add(LPAREN292);

            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:572:12: ( (dist= KW_DISTINCT )? expression ( COMMA expression )* )?
            int alt84=2;
            int LA84_0 = input.LA(1);

            if ( (LA84_0==StringLiteral||(LA84_0>=Identifier && LA84_0<=LPAREN)||LA84_0==Number||LA84_0==KW_DISTINCT||LA84_0==DOT||(LA84_0>=KW_CAST && LA84_0<=CharSetName)||LA84_0==KW_NULL||(LA84_0>=PLUS && LA84_0<=TILDE)||LA84_0==KW_NOT||(LA84_0>=KW_TRUE && LA84_0<=KW_FALSE)) ) {
                alt84=1;
            }
            switch (alt84) {
                case 1 :
                    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:573:11: (dist= KW_DISTINCT )? expression ( COMMA expression )*
                    {
                    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:573:11: (dist= KW_DISTINCT )?
                    int alt82=2;
                    int LA82_0 = input.LA(1);

                    if ( (LA82_0==KW_DISTINCT) ) {
                        alt82=1;
                    }
                    switch (alt82) {
                        case 1 :
                            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:573:12: dist= KW_DISTINCT
                            {
                            dist=(Token)input.LT(1);
                            match(input,KW_DISTINCT,FOLLOW_KW_DISTINCT_in_function4144); if (failed) return retval;
                            if ( backtracking==0 ) stream_KW_DISTINCT.add(dist);


                            }
                            break;

                    }

                    pushFollow(FOLLOW_expression_in_function4158);
                    expression293=expression();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_expression.add(expression293.getTree());
                    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:575:11: ( COMMA expression )*
                    loop83:
                    do {
                        int alt83=2;
                        int LA83_0 = input.LA(1);

                        if ( (LA83_0==COMMA) ) {
                            alt83=1;
                        }


                        switch (alt83) {
                    	case 1 :
                    	    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:575:12: COMMA expression
                    	    {
                    	    COMMA294=(Token)input.LT(1);
                    	    match(input,COMMA,FOLLOW_COMMA_in_function4171); if (failed) return retval;
                    	    if ( backtracking==0 ) stream_COMMA.add(COMMA294);

                    	    pushFollow(FOLLOW_expression_in_function4173);
                    	    expression295=expression();
                    	    _fsp--;
                    	    if (failed) return retval;
                    	    if ( backtracking==0 ) stream_expression.add(expression295.getTree());

                    	    }
                    	    break;

                    	default :
                    	    break loop83;
                        }
                    } while (true);


                    }
                    break;

            }

            RPAREN296=(Token)input.LT(1);
            match(input,RPAREN,FOLLOW_RPAREN_in_function4192); if (failed) return retval;
            if ( backtracking==0 ) stream_RPAREN.add(RPAREN296);


            // AST REWRITE
            // elements: Identifier, Identifier, expression, expression
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 577:12: -> {$dist == null}? ^( TOK_FUNCTION Identifier ( expression )+ )
            if (dist == null) {
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:577:32: ^( TOK_FUNCTION Identifier ( expression )+ )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(adaptor.create(TOK_FUNCTION, "TOK_FUNCTION"), root_1);

                adaptor.addChild(root_1, stream_Identifier.next());
                if ( !(stream_expression.hasNext()) ) {
                    throw new RewriteEarlyExitException();
                }
                while ( stream_expression.hasNext() ) {
                    adaptor.addChild(root_1, stream_expression.next());

                }
                stream_expression.reset();

                adaptor.addChild(root_0, root_1);
                }

            }
            else // 578:27: -> ^( TOK_FUNCTIONDI Identifier ( expression )+ )
            {
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:578:30: ^( TOK_FUNCTIONDI Identifier ( expression )+ )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(adaptor.create(TOK_FUNCTIONDI, "TOK_FUNCTIONDI"), root_1);

                adaptor.addChild(root_1, stream_Identifier.next());
                if ( !(stream_expression.hasNext()) ) {
                    throw new RewriteEarlyExitException();
                }
                while ( stream_expression.hasNext() ) {
                    adaptor.addChild(root_1, stream_expression.next());

                }
                stream_expression.reset();

                adaptor.addChild(root_0, root_1);
                }

            }

            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e) {
          reportError(e);
          throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end function

    public static class castExpression_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start castExpression
    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:582:1: castExpression : KW_CAST LPAREN expression KW_AS primitiveType RPAREN -> ^( TOK_FUNCTION primitiveType expression ) ;
    public final castExpression_return castExpression() throws RecognitionException {
        castExpression_return retval = new castExpression_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token KW_CAST297=null;
        Token LPAREN298=null;
        Token KW_AS300=null;
        Token RPAREN302=null;
        expression_return expression299 = null;

        primitiveType_return primitiveType301 = null;


        CommonTree KW_CAST297_tree=null;
        CommonTree LPAREN298_tree=null;
        CommonTree KW_AS300_tree=null;
        CommonTree RPAREN302_tree=null;
        RewriteRuleTokenStream stream_KW_AS=new RewriteRuleTokenStream(adaptor,"token KW_AS");
        RewriteRuleTokenStream stream_RPAREN=new RewriteRuleTokenStream(adaptor,"token RPAREN");
        RewriteRuleTokenStream stream_KW_CAST=new RewriteRuleTokenStream(adaptor,"token KW_CAST");
        RewriteRuleTokenStream stream_LPAREN=new RewriteRuleTokenStream(adaptor,"token LPAREN");
        RewriteRuleSubtreeStream stream_expression=new RewriteRuleSubtreeStream(adaptor,"rule expression");
        RewriteRuleSubtreeStream stream_primitiveType=new RewriteRuleSubtreeStream(adaptor,"rule primitiveType");
        try {
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:583:5: ( KW_CAST LPAREN expression KW_AS primitiveType RPAREN -> ^( TOK_FUNCTION primitiveType expression ) )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:584:5: KW_CAST LPAREN expression KW_AS primitiveType RPAREN
            {
            KW_CAST297=(Token)input.LT(1);
            match(input,KW_CAST,FOLLOW_KW_CAST_in_castExpression4264); if (failed) return retval;
            if ( backtracking==0 ) stream_KW_CAST.add(KW_CAST297);

            LPAREN298=(Token)input.LT(1);
            match(input,LPAREN,FOLLOW_LPAREN_in_castExpression4270); if (failed) return retval;
            if ( backtracking==0 ) stream_LPAREN.add(LPAREN298);

            pushFollow(FOLLOW_expression_in_castExpression4283);
            expression299=expression();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_expression.add(expression299.getTree());
            KW_AS300=(Token)input.LT(1);
            match(input,KW_AS,FOLLOW_KW_AS_in_castExpression4295); if (failed) return retval;
            if ( backtracking==0 ) stream_KW_AS.add(KW_AS300);

            pushFollow(FOLLOW_primitiveType_in_castExpression4307);
            primitiveType301=primitiveType();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_primitiveType.add(primitiveType301.getTree());
            RPAREN302=(Token)input.LT(1);
            match(input,RPAREN,FOLLOW_RPAREN_in_castExpression4313); if (failed) return retval;
            if ( backtracking==0 ) stream_RPAREN.add(RPAREN302);


            // AST REWRITE
            // elements: expression, primitiveType
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 589:12: -> ^( TOK_FUNCTION primitiveType expression )
            {
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:589:15: ^( TOK_FUNCTION primitiveType expression )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(adaptor.create(TOK_FUNCTION, "TOK_FUNCTION"), root_1);

                adaptor.addChild(root_1, stream_primitiveType.next());
                adaptor.addChild(root_1, stream_expression.next());

                adaptor.addChild(root_0, root_1);
                }

            }

            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e) {
          reportError(e);
          throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end castExpression

    public static class constant_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start constant
    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:592:1: constant : ( Number | StringLiteral | charSetStringLiteral | booleanValue );
    public final constant_return constant() throws RecognitionException {
        constant_return retval = new constant_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token Number303=null;
        Token StringLiteral304=null;
        charSetStringLiteral_return charSetStringLiteral305 = null;

        booleanValue_return booleanValue306 = null;


        CommonTree Number303_tree=null;
        CommonTree StringLiteral304_tree=null;

        try {
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:593:5: ( Number | StringLiteral | charSetStringLiteral | booleanValue )
            int alt85=4;
            switch ( input.LA(1) ) {
            case Number:
                {
                alt85=1;
                }
                break;
            case StringLiteral:
                {
                alt85=2;
                }
                break;
            case CharSetName:
                {
                alt85=3;
                }
                break;
            case KW_TRUE:
            case KW_FALSE:
                {
                alt85=4;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("592:1: constant : ( Number | StringLiteral | charSetStringLiteral | booleanValue );", 85, 0, input);

                throw nvae;
            }

            switch (alt85) {
                case 1 :
                    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:594:5: Number
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    Number303=(Token)input.LT(1);
                    match(input,Number,FOLLOW_Number_in_constant4348); if (failed) return retval;
                    if ( backtracking==0 ) {
                    Number303_tree = (CommonTree)adaptor.create(Number303);
                    adaptor.addChild(root_0, Number303_tree);
                    }

                    }
                    break;
                case 2 :
                    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:595:7: StringLiteral
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    StringLiteral304=(Token)input.LT(1);
                    match(input,StringLiteral,FOLLOW_StringLiteral_in_constant4356); if (failed) return retval;
                    if ( backtracking==0 ) {
                    StringLiteral304_tree = (CommonTree)adaptor.create(StringLiteral304);
                    adaptor.addChild(root_0, StringLiteral304_tree);
                    }

                    }
                    break;
                case 3 :
                    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:596:7: charSetStringLiteral
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_charSetStringLiteral_in_constant4364);
                    charSetStringLiteral305=charSetStringLiteral();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, charSetStringLiteral305.getTree());

                    }
                    break;
                case 4 :
                    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:597:7: booleanValue
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_booleanValue_in_constant4372);
                    booleanValue306=booleanValue();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, booleanValue306.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e) {
          reportError(e);
          throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end constant

    public static class charSetStringLiteral_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start charSetStringLiteral
    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:600:1: charSetStringLiteral : csName= CharSetName csLiteral= CharSetLiteral -> ^( TOK_CHARSETLITERAL $csName $csLiteral) ;
    public final charSetStringLiteral_return charSetStringLiteral() throws RecognitionException {
        charSetStringLiteral_return retval = new charSetStringLiteral_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token csName=null;
        Token csLiteral=null;

        CommonTree csName_tree=null;
        CommonTree csLiteral_tree=null;
        RewriteRuleTokenStream stream_CharSetLiteral=new RewriteRuleTokenStream(adaptor,"token CharSetLiteral");
        RewriteRuleTokenStream stream_CharSetName=new RewriteRuleTokenStream(adaptor,"token CharSetName");

        try {
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:601:5: (csName= CharSetName csLiteral= CharSetLiteral -> ^( TOK_CHARSETLITERAL $csName $csLiteral) )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:602:5: csName= CharSetName csLiteral= CharSetLiteral
            {
            csName=(Token)input.LT(1);
            match(input,CharSetName,FOLLOW_CharSetName_in_charSetStringLiteral4396); if (failed) return retval;
            if ( backtracking==0 ) stream_CharSetName.add(csName);

            csLiteral=(Token)input.LT(1);
            match(input,CharSetLiteral,FOLLOW_CharSetLiteral_in_charSetStringLiteral4400); if (failed) return retval;
            if ( backtracking==0 ) stream_CharSetLiteral.add(csLiteral);


            // AST REWRITE
            // elements: csLiteral, csName
            // token labels: csName, csLiteral
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleTokenStream stream_csName=new RewriteRuleTokenStream(adaptor,"token csName",csName);
            RewriteRuleTokenStream stream_csLiteral=new RewriteRuleTokenStream(adaptor,"token csLiteral",csLiteral);
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 602:49: -> ^( TOK_CHARSETLITERAL $csName $csLiteral)
            {
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:602:52: ^( TOK_CHARSETLITERAL $csName $csLiteral)
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(adaptor.create(TOK_CHARSETLITERAL, "TOK_CHARSETLITERAL"), root_1);

                adaptor.addChild(root_1, stream_csName.next());
                adaptor.addChild(root_1, stream_csLiteral.next());

                adaptor.addChild(root_0, root_1);
                }

            }

            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e) {
          reportError(e);
          throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end charSetStringLiteral

    public static class expression_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start expression
    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:605:1: expression : precedenceOrExpression ;
    public final expression_return expression() throws RecognitionException {
        expression_return retval = new expression_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        precedenceOrExpression_return precedenceOrExpression307 = null;



        try {
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:605:11: ( precedenceOrExpression )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:606:5: precedenceOrExpression
            {
            root_0 = (CommonTree)adaptor.nil();

            pushFollow(FOLLOW_precedenceOrExpression_in_expression4428);
            precedenceOrExpression307=precedenceOrExpression();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) adaptor.addChild(root_0, precedenceOrExpression307.getTree());

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e) {
          reportError(e);
          throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end expression

    public static class atomExpression_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start atomExpression
    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:609:1: atomExpression : ( KW_NULL -> TOK_NULL | constant | function | castExpression | tableColumn | LPAREN expression RPAREN );
    public final atomExpression_return atomExpression() throws RecognitionException {
        atomExpression_return retval = new atomExpression_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token KW_NULL308=null;
        Token LPAREN313=null;
        Token RPAREN315=null;
        constant_return constant309 = null;

        function_return function310 = null;

        castExpression_return castExpression311 = null;

        tableColumn_return tableColumn312 = null;

        expression_return expression314 = null;


        CommonTree KW_NULL308_tree=null;
        CommonTree LPAREN313_tree=null;
        CommonTree RPAREN315_tree=null;
        RewriteRuleTokenStream stream_KW_NULL=new RewriteRuleTokenStream(adaptor,"token KW_NULL");

        try {
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:609:15: ( KW_NULL -> TOK_NULL | constant | function | castExpression | tableColumn | LPAREN expression RPAREN )
            int alt86=6;
            switch ( input.LA(1) ) {
            case KW_NULL:
                {
                alt86=1;
                }
                break;
            case StringLiteral:
            case Number:
            case CharSetName:
            case KW_TRUE:
            case KW_FALSE:
                {
                alt86=2;
                }
                break;
            case Identifier:
                {
                int LA86_3 = input.LA(2);

                if ( (LA86_3==LPAREN) ) {
                    alt86=3;
                }
                else if ( (LA86_3==DOT) ) {
                    alt86=5;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("609:1: atomExpression : ( KW_NULL -> TOK_NULL | constant | function | castExpression | tableColumn | LPAREN expression RPAREN );", 86, 3, input);

                    throw nvae;
                }
                }
                break;
            case KW_CAST:
                {
                alt86=4;
                }
                break;
            case DOT:
                {
                alt86=5;
                }
                break;
            case LPAREN:
                {
                alt86=6;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("609:1: atomExpression : ( KW_NULL -> TOK_NULL | constant | function | castExpression | tableColumn | LPAREN expression RPAREN );", 86, 0, input);

                throw nvae;
            }

            switch (alt86) {
                case 1 :
                    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:610:5: KW_NULL
                    {
                    KW_NULL308=(Token)input.LT(1);
                    match(input,KW_NULL,FOLLOW_KW_NULL_in_atomExpression4444); if (failed) return retval;
                    if ( backtracking==0 ) stream_KW_NULL.add(KW_NULL308);


                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    if ( backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 610:13: -> TOK_NULL
                    {
                        adaptor.addChild(root_0, adaptor.create(TOK_NULL, "TOK_NULL"));

                    }

                    }

                    }
                    break;
                case 2 :
                    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:611:7: constant
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_constant_in_atomExpression4456);
                    constant309=constant();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, constant309.getTree());

                    }
                    break;
                case 3 :
                    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:612:7: function
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_function_in_atomExpression4464);
                    function310=function();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, function310.getTree());

                    }
                    break;
                case 4 :
                    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:613:7: castExpression
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_castExpression_in_atomExpression4472);
                    castExpression311=castExpression();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, castExpression311.getTree());

                    }
                    break;
                case 5 :
                    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:614:7: tableColumn
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_tableColumn_in_atomExpression4480);
                    tableColumn312=tableColumn();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, tableColumn312.getTree());

                    }
                    break;
                case 6 :
                    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:615:7: LPAREN expression RPAREN
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    LPAREN313=(Token)input.LT(1);
                    match(input,LPAREN,FOLLOW_LPAREN_in_atomExpression4488); if (failed) return retval;
                    pushFollow(FOLLOW_expression_in_atomExpression4491);
                    expression314=expression();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, expression314.getTree());
                    RPAREN315=(Token)input.LT(1);
                    match(input,RPAREN,FOLLOW_RPAREN_in_atomExpression4493); if (failed) return retval;

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e) {
          reportError(e);
          throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end atomExpression

    public static class precedenceFieldExpression_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start precedenceFieldExpression
    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:619:1: precedenceFieldExpression : atomExpression ( ( LSQUARE expression RSQUARE ) | ( DOT Identifier ) )* ;
    public final precedenceFieldExpression_return precedenceFieldExpression() throws RecognitionException {
        precedenceFieldExpression_return retval = new precedenceFieldExpression_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token LSQUARE317=null;
        Token RSQUARE319=null;
        Token DOT320=null;
        Token Identifier321=null;
        atomExpression_return atomExpression316 = null;

        expression_return expression318 = null;


        CommonTree LSQUARE317_tree=null;
        CommonTree RSQUARE319_tree=null;
        CommonTree DOT320_tree=null;
        CommonTree Identifier321_tree=null;

        try {
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:620:5: ( atomExpression ( ( LSQUARE expression RSQUARE ) | ( DOT Identifier ) )* )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:621:5: atomExpression ( ( LSQUARE expression RSQUARE ) | ( DOT Identifier ) )*
            {
            root_0 = (CommonTree)adaptor.nil();

            pushFollow(FOLLOW_atomExpression_in_precedenceFieldExpression4516);
            atomExpression316=atomExpression();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) adaptor.addChild(root_0, atomExpression316.getTree());
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:621:20: ( ( LSQUARE expression RSQUARE ) | ( DOT Identifier ) )*
            loop87:
            do {
                int alt87=3;
                int LA87_0 = input.LA(1);

                if ( (LA87_0==LSQUARE) ) {
                    alt87=1;
                }
                else if ( (LA87_0==DOT) ) {
                    alt87=2;
                }


                switch (alt87) {
            	case 1 :
            	    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:621:21: ( LSQUARE expression RSQUARE )
            	    {
            	    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:621:21: ( LSQUARE expression RSQUARE )
            	    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:621:22: LSQUARE expression RSQUARE
            	    {
            	    LSQUARE317=(Token)input.LT(1);
            	    match(input,LSQUARE,FOLLOW_LSQUARE_in_precedenceFieldExpression4520); if (failed) return retval;
            	    if ( backtracking==0 ) {
            	    LSQUARE317_tree = (CommonTree)adaptor.create(LSQUARE317);
            	    root_0 = (CommonTree)adaptor.becomeRoot(LSQUARE317_tree, root_0);
            	    }
            	    pushFollow(FOLLOW_expression_in_precedenceFieldExpression4523);
            	    expression318=expression();
            	    _fsp--;
            	    if (failed) return retval;
            	    if ( backtracking==0 ) adaptor.addChild(root_0, expression318.getTree());
            	    RSQUARE319=(Token)input.LT(1);
            	    match(input,RSQUARE,FOLLOW_RSQUARE_in_precedenceFieldExpression4525); if (failed) return retval;

            	    }


            	    }
            	    break;
            	case 2 :
            	    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:621:54: ( DOT Identifier )
            	    {
            	    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:621:54: ( DOT Identifier )
            	    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:621:55: DOT Identifier
            	    {
            	    DOT320=(Token)input.LT(1);
            	    match(input,DOT,FOLLOW_DOT_in_precedenceFieldExpression4532); if (failed) return retval;
            	    if ( backtracking==0 ) {
            	    DOT320_tree = (CommonTree)adaptor.create(DOT320);
            	    root_0 = (CommonTree)adaptor.becomeRoot(DOT320_tree, root_0);
            	    }
            	    Identifier321=(Token)input.LT(1);
            	    match(input,Identifier,FOLLOW_Identifier_in_precedenceFieldExpression4535); if (failed) return retval;
            	    if ( backtracking==0 ) {
            	    Identifier321_tree = (CommonTree)adaptor.create(Identifier321);
            	    adaptor.addChild(root_0, Identifier321_tree);
            	    }

            	    }


            	    }
            	    break;

            	default :
            	    break loop87;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e) {
          reportError(e);
          throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end precedenceFieldExpression

    public static class precedenceUnaryOperator_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start precedenceUnaryOperator
    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:624:1: precedenceUnaryOperator : ( PLUS | MINUS | TILDE );
    public final precedenceUnaryOperator_return precedenceUnaryOperator() throws RecognitionException {
        precedenceUnaryOperator_return retval = new precedenceUnaryOperator_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token set322=null;

        CommonTree set322_tree=null;

        try {
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:625:5: ( PLUS | MINUS | TILDE )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:
            {
            root_0 = (CommonTree)adaptor.nil();

            set322=(Token)input.LT(1);
            if ( (input.LA(1)>=PLUS && input.LA(1)<=TILDE) ) {
                input.consume();
                if ( backtracking==0 ) adaptor.addChild(root_0, adaptor.create(set322));
                errorRecovery=false;failed=false;
            }
            else {
                if (backtracking>0) {failed=true; return retval;}
                MismatchedSetException mse =
                    new MismatchedSetException(null,input);
                recoverFromMismatchedSet(input,mse,FOLLOW_set_in_precedenceUnaryOperator0);    throw mse;
            }


            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e) {
          reportError(e);
          throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end precedenceUnaryOperator

    public static class precedenceUnaryExpression_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start precedenceUnaryExpression
    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:629:1: precedenceUnaryExpression : ( precedenceFieldExpression KW_IS KW_NULL -> ^( TOK_FUNCTION TOK_ISNULL precedenceFieldExpression ) | precedenceFieldExpression KW_IS KW_NOT KW_NULL -> ^( TOK_FUNCTION TOK_ISNOTNULL precedenceFieldExpression ) | ( precedenceUnaryOperator )* precedenceFieldExpression );
    public final precedenceUnaryExpression_return precedenceUnaryExpression() throws RecognitionException {
        precedenceUnaryExpression_return retval = new precedenceUnaryExpression_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token KW_IS324=null;
        Token KW_NULL325=null;
        Token KW_IS327=null;
        Token KW_NOT328=null;
        Token KW_NULL329=null;
        precedenceFieldExpression_return precedenceFieldExpression323 = null;

        precedenceFieldExpression_return precedenceFieldExpression326 = null;

        precedenceUnaryOperator_return precedenceUnaryOperator330 = null;

        precedenceFieldExpression_return precedenceFieldExpression331 = null;


        CommonTree KW_IS324_tree=null;
        CommonTree KW_NULL325_tree=null;
        CommonTree KW_IS327_tree=null;
        CommonTree KW_NOT328_tree=null;
        CommonTree KW_NULL329_tree=null;
        RewriteRuleTokenStream stream_KW_IS=new RewriteRuleTokenStream(adaptor,"token KW_IS");
        RewriteRuleTokenStream stream_KW_NULL=new RewriteRuleTokenStream(adaptor,"token KW_NULL");
        RewriteRuleTokenStream stream_KW_NOT=new RewriteRuleTokenStream(adaptor,"token KW_NOT");
        RewriteRuleSubtreeStream stream_precedenceFieldExpression=new RewriteRuleSubtreeStream(adaptor,"rule precedenceFieldExpression");
        try {
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:630:5: ( precedenceFieldExpression KW_IS KW_NULL -> ^( TOK_FUNCTION TOK_ISNULL precedenceFieldExpression ) | precedenceFieldExpression KW_IS KW_NOT KW_NULL -> ^( TOK_FUNCTION TOK_ISNOTNULL precedenceFieldExpression ) | ( precedenceUnaryOperator )* precedenceFieldExpression )
            int alt89=3;
            switch ( input.LA(1) ) {
            case KW_NULL:
                {
                int LA89_1 = input.LA(2);

                if ( (synpred119()) ) {
                    alt89=1;
                }
                else if ( (synpred120()) ) {
                    alt89=2;
                }
                else if ( (true) ) {
                    alt89=3;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("629:1: precedenceUnaryExpression : ( precedenceFieldExpression KW_IS KW_NULL -> ^( TOK_FUNCTION TOK_ISNULL precedenceFieldExpression ) | precedenceFieldExpression KW_IS KW_NOT KW_NULL -> ^( TOK_FUNCTION TOK_ISNOTNULL precedenceFieldExpression ) | ( precedenceUnaryOperator )* precedenceFieldExpression );", 89, 1, input);

                    throw nvae;
                }
                }
                break;
            case Number:
                {
                int LA89_2 = input.LA(2);

                if ( (synpred119()) ) {
                    alt89=1;
                }
                else if ( (synpred120()) ) {
                    alt89=2;
                }
                else if ( (true) ) {
                    alt89=3;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("629:1: precedenceUnaryExpression : ( precedenceFieldExpression KW_IS KW_NULL -> ^( TOK_FUNCTION TOK_ISNULL precedenceFieldExpression ) | precedenceFieldExpression KW_IS KW_NOT KW_NULL -> ^( TOK_FUNCTION TOK_ISNOTNULL precedenceFieldExpression ) | ( precedenceUnaryOperator )* precedenceFieldExpression );", 89, 2, input);

                    throw nvae;
                }
                }
                break;
            case StringLiteral:
                {
                int LA89_3 = input.LA(2);

                if ( (synpred119()) ) {
                    alt89=1;
                }
                else if ( (synpred120()) ) {
                    alt89=2;
                }
                else if ( (true) ) {
                    alt89=3;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("629:1: precedenceUnaryExpression : ( precedenceFieldExpression KW_IS KW_NULL -> ^( TOK_FUNCTION TOK_ISNULL precedenceFieldExpression ) | precedenceFieldExpression KW_IS KW_NOT KW_NULL -> ^( TOK_FUNCTION TOK_ISNOTNULL precedenceFieldExpression ) | ( precedenceUnaryOperator )* precedenceFieldExpression );", 89, 3, input);

                    throw nvae;
                }
                }
                break;
            case CharSetName:
                {
                int LA89_4 = input.LA(2);

                if ( (synpred119()) ) {
                    alt89=1;
                }
                else if ( (synpred120()) ) {
                    alt89=2;
                }
                else if ( (true) ) {
                    alt89=3;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("629:1: precedenceUnaryExpression : ( precedenceFieldExpression KW_IS KW_NULL -> ^( TOK_FUNCTION TOK_ISNULL precedenceFieldExpression ) | precedenceFieldExpression KW_IS KW_NOT KW_NULL -> ^( TOK_FUNCTION TOK_ISNOTNULL precedenceFieldExpression ) | ( precedenceUnaryOperator )* precedenceFieldExpression );", 89, 4, input);

                    throw nvae;
                }
                }
                break;
            case KW_TRUE:
                {
                int LA89_5 = input.LA(2);

                if ( (synpred119()) ) {
                    alt89=1;
                }
                else if ( (synpred120()) ) {
                    alt89=2;
                }
                else if ( (true) ) {
                    alt89=3;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("629:1: precedenceUnaryExpression : ( precedenceFieldExpression KW_IS KW_NULL -> ^( TOK_FUNCTION TOK_ISNULL precedenceFieldExpression ) | precedenceFieldExpression KW_IS KW_NOT KW_NULL -> ^( TOK_FUNCTION TOK_ISNOTNULL precedenceFieldExpression ) | ( precedenceUnaryOperator )* precedenceFieldExpression );", 89, 5, input);

                    throw nvae;
                }
                }
                break;
            case KW_FALSE:
                {
                int LA89_6 = input.LA(2);

                if ( (synpred119()) ) {
                    alt89=1;
                }
                else if ( (synpred120()) ) {
                    alt89=2;
                }
                else if ( (true) ) {
                    alt89=3;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("629:1: precedenceUnaryExpression : ( precedenceFieldExpression KW_IS KW_NULL -> ^( TOK_FUNCTION TOK_ISNULL precedenceFieldExpression ) | precedenceFieldExpression KW_IS KW_NOT KW_NULL -> ^( TOK_FUNCTION TOK_ISNOTNULL precedenceFieldExpression ) | ( precedenceUnaryOperator )* precedenceFieldExpression );", 89, 6, input);

                    throw nvae;
                }
                }
                break;
            case Identifier:
                {
                int LA89_7 = input.LA(2);

                if ( (synpred119()) ) {
                    alt89=1;
                }
                else if ( (synpred120()) ) {
                    alt89=2;
                }
                else if ( (true) ) {
                    alt89=3;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("629:1: precedenceUnaryExpression : ( precedenceFieldExpression KW_IS KW_NULL -> ^( TOK_FUNCTION TOK_ISNULL precedenceFieldExpression ) | precedenceFieldExpression KW_IS KW_NOT KW_NULL -> ^( TOK_FUNCTION TOK_ISNOTNULL precedenceFieldExpression ) | ( precedenceUnaryOperator )* precedenceFieldExpression );", 89, 7, input);

                    throw nvae;
                }
                }
                break;
            case KW_CAST:
                {
                int LA89_8 = input.LA(2);

                if ( (synpred119()) ) {
                    alt89=1;
                }
                else if ( (synpred120()) ) {
                    alt89=2;
                }
                else if ( (true) ) {
                    alt89=3;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("629:1: precedenceUnaryExpression : ( precedenceFieldExpression KW_IS KW_NULL -> ^( TOK_FUNCTION TOK_ISNULL precedenceFieldExpression ) | precedenceFieldExpression KW_IS KW_NOT KW_NULL -> ^( TOK_FUNCTION TOK_ISNOTNULL precedenceFieldExpression ) | ( precedenceUnaryOperator )* precedenceFieldExpression );", 89, 8, input);

                    throw nvae;
                }
                }
                break;
            case DOT:
                {
                int LA89_9 = input.LA(2);

                if ( (synpred119()) ) {
                    alt89=1;
                }
                else if ( (synpred120()) ) {
                    alt89=2;
                }
                else if ( (true) ) {
                    alt89=3;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("629:1: precedenceUnaryExpression : ( precedenceFieldExpression KW_IS KW_NULL -> ^( TOK_FUNCTION TOK_ISNULL precedenceFieldExpression ) | precedenceFieldExpression KW_IS KW_NOT KW_NULL -> ^( TOK_FUNCTION TOK_ISNOTNULL precedenceFieldExpression ) | ( precedenceUnaryOperator )* precedenceFieldExpression );", 89, 9, input);

                    throw nvae;
                }
                }
                break;
            case LPAREN:
                {
                int LA89_10 = input.LA(2);

                if ( (synpred119()) ) {
                    alt89=1;
                }
                else if ( (synpred120()) ) {
                    alt89=2;
                }
                else if ( (true) ) {
                    alt89=3;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("629:1: precedenceUnaryExpression : ( precedenceFieldExpression KW_IS KW_NULL -> ^( TOK_FUNCTION TOK_ISNULL precedenceFieldExpression ) | precedenceFieldExpression KW_IS KW_NOT KW_NULL -> ^( TOK_FUNCTION TOK_ISNOTNULL precedenceFieldExpression ) | ( precedenceUnaryOperator )* precedenceFieldExpression );", 89, 10, input);

                    throw nvae;
                }
                }
                break;
            case PLUS:
            case MINUS:
            case TILDE:
                {
                alt89=3;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("629:1: precedenceUnaryExpression : ( precedenceFieldExpression KW_IS KW_NULL -> ^( TOK_FUNCTION TOK_ISNULL precedenceFieldExpression ) | precedenceFieldExpression KW_IS KW_NOT KW_NULL -> ^( TOK_FUNCTION TOK_ISNOTNULL precedenceFieldExpression ) | ( precedenceUnaryOperator )* precedenceFieldExpression );", 89, 0, input);

                throw nvae;
            }

            switch (alt89) {
                case 1 :
                    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:631:7: precedenceFieldExpression KW_IS KW_NULL
                    {
                    pushFollow(FOLLOW_precedenceFieldExpression_in_precedenceUnaryExpression4590);
                    precedenceFieldExpression323=precedenceFieldExpression();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_precedenceFieldExpression.add(precedenceFieldExpression323.getTree());
                    KW_IS324=(Token)input.LT(1);
                    match(input,KW_IS,FOLLOW_KW_IS_in_precedenceUnaryExpression4592); if (failed) return retval;
                    if ( backtracking==0 ) stream_KW_IS.add(KW_IS324);

                    KW_NULL325=(Token)input.LT(1);
                    match(input,KW_NULL,FOLLOW_KW_NULL_in_precedenceUnaryExpression4594); if (failed) return retval;
                    if ( backtracking==0 ) stream_KW_NULL.add(KW_NULL325);


                    // AST REWRITE
                    // elements: precedenceFieldExpression
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    if ( backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 631:47: -> ^( TOK_FUNCTION TOK_ISNULL precedenceFieldExpression )
                    {
                        // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:631:50: ^( TOK_FUNCTION TOK_ISNULL precedenceFieldExpression )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot(adaptor.create(TOK_FUNCTION, "TOK_FUNCTION"), root_1);

                        adaptor.addChild(root_1, adaptor.create(TOK_ISNULL, "TOK_ISNULL"));
                        adaptor.addChild(root_1, stream_precedenceFieldExpression.next());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    }

                    }
                    break;
                case 2 :
                    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:632:7: precedenceFieldExpression KW_IS KW_NOT KW_NULL
                    {
                    pushFollow(FOLLOW_precedenceFieldExpression_in_precedenceUnaryExpression4612);
                    precedenceFieldExpression326=precedenceFieldExpression();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_precedenceFieldExpression.add(precedenceFieldExpression326.getTree());
                    KW_IS327=(Token)input.LT(1);
                    match(input,KW_IS,FOLLOW_KW_IS_in_precedenceUnaryExpression4614); if (failed) return retval;
                    if ( backtracking==0 ) stream_KW_IS.add(KW_IS327);

                    KW_NOT328=(Token)input.LT(1);
                    match(input,KW_NOT,FOLLOW_KW_NOT_in_precedenceUnaryExpression4616); if (failed) return retval;
                    if ( backtracking==0 ) stream_KW_NOT.add(KW_NOT328);

                    KW_NULL329=(Token)input.LT(1);
                    match(input,KW_NULL,FOLLOW_KW_NULL_in_precedenceUnaryExpression4618); if (failed) return retval;
                    if ( backtracking==0 ) stream_KW_NULL.add(KW_NULL329);


                    // AST REWRITE
                    // elements: precedenceFieldExpression
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    if ( backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 632:54: -> ^( TOK_FUNCTION TOK_ISNOTNULL precedenceFieldExpression )
                    {
                        // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:632:57: ^( TOK_FUNCTION TOK_ISNOTNULL precedenceFieldExpression )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot(adaptor.create(TOK_FUNCTION, "TOK_FUNCTION"), root_1);

                        adaptor.addChild(root_1, adaptor.create(TOK_ISNOTNULL, "TOK_ISNOTNULL"));
                        adaptor.addChild(root_1, stream_precedenceFieldExpression.next());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    }

                    }
                    break;
                case 3 :
                    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:633:7: ( precedenceUnaryOperator )* precedenceFieldExpression
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:633:7: ( precedenceUnaryOperator )*
                    loop88:
                    do {
                        int alt88=2;
                        int LA88_0 = input.LA(1);

                        if ( ((LA88_0>=PLUS && LA88_0<=TILDE)) ) {
                            alt88=1;
                        }


                        switch (alt88) {
                    	case 1 :
                    	    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:633:8: precedenceUnaryOperator
                    	    {
                    	    pushFollow(FOLLOW_precedenceUnaryOperator_in_precedenceUnaryExpression4637);
                    	    precedenceUnaryOperator330=precedenceUnaryOperator();
                    	    _fsp--;
                    	    if (failed) return retval;
                    	    if ( backtracking==0 ) root_0 = (CommonTree)adaptor.becomeRoot(precedenceUnaryOperator330.getTree(), root_0);

                    	    }
                    	    break;

                    	default :
                    	    break loop88;
                        }
                    } while (true);

                    pushFollow(FOLLOW_precedenceFieldExpression_in_precedenceUnaryExpression4642);
                    precedenceFieldExpression331=precedenceFieldExpression();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, precedenceFieldExpression331.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e) {
          reportError(e);
          throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end precedenceUnaryExpression

    public static class precedenceBitwiseXorOperator_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start precedenceBitwiseXorOperator
    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:637:1: precedenceBitwiseXorOperator : BITWISEXOR ;
    public final precedenceBitwiseXorOperator_return precedenceBitwiseXorOperator() throws RecognitionException {
        precedenceBitwiseXorOperator_return retval = new precedenceBitwiseXorOperator_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token BITWISEXOR332=null;

        CommonTree BITWISEXOR332_tree=null;

        try {
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:638:5: ( BITWISEXOR )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:639:5: BITWISEXOR
            {
            root_0 = (CommonTree)adaptor.nil();

            BITWISEXOR332=(Token)input.LT(1);
            match(input,BITWISEXOR,FOLLOW_BITWISEXOR_in_precedenceBitwiseXorOperator4664); if (failed) return retval;
            if ( backtracking==0 ) {
            BITWISEXOR332_tree = (CommonTree)adaptor.create(BITWISEXOR332);
            adaptor.addChild(root_0, BITWISEXOR332_tree);
            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e) {
          reportError(e);
          throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end precedenceBitwiseXorOperator

    public static class precedenceBitwiseXorExpression_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start precedenceBitwiseXorExpression
    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:642:1: precedenceBitwiseXorExpression : precedenceUnaryExpression ( precedenceBitwiseXorOperator precedenceUnaryExpression )* ;
    public final precedenceBitwiseXorExpression_return precedenceBitwiseXorExpression() throws RecognitionException {
        precedenceBitwiseXorExpression_return retval = new precedenceBitwiseXorExpression_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        precedenceUnaryExpression_return precedenceUnaryExpression333 = null;

        precedenceBitwiseXorOperator_return precedenceBitwiseXorOperator334 = null;

        precedenceUnaryExpression_return precedenceUnaryExpression335 = null;



        try {
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:643:5: ( precedenceUnaryExpression ( precedenceBitwiseXorOperator precedenceUnaryExpression )* )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:644:5: precedenceUnaryExpression ( precedenceBitwiseXorOperator precedenceUnaryExpression )*
            {
            root_0 = (CommonTree)adaptor.nil();

            pushFollow(FOLLOW_precedenceUnaryExpression_in_precedenceBitwiseXorExpression4685);
            precedenceUnaryExpression333=precedenceUnaryExpression();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) adaptor.addChild(root_0, precedenceUnaryExpression333.getTree());
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:644:31: ( precedenceBitwiseXorOperator precedenceUnaryExpression )*
            loop90:
            do {
                int alt90=2;
                int LA90_0 = input.LA(1);

                if ( (LA90_0==BITWISEXOR) ) {
                    alt90=1;
                }


                switch (alt90) {
            	case 1 :
            	    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:644:32: precedenceBitwiseXorOperator precedenceUnaryExpression
            	    {
            	    pushFollow(FOLLOW_precedenceBitwiseXorOperator_in_precedenceBitwiseXorExpression4688);
            	    precedenceBitwiseXorOperator334=precedenceBitwiseXorOperator();
            	    _fsp--;
            	    if (failed) return retval;
            	    if ( backtracking==0 ) root_0 = (CommonTree)adaptor.becomeRoot(precedenceBitwiseXorOperator334.getTree(), root_0);
            	    pushFollow(FOLLOW_precedenceUnaryExpression_in_precedenceBitwiseXorExpression4691);
            	    precedenceUnaryExpression335=precedenceUnaryExpression();
            	    _fsp--;
            	    if (failed) return retval;
            	    if ( backtracking==0 ) adaptor.addChild(root_0, precedenceUnaryExpression335.getTree());

            	    }
            	    break;

            	default :
            	    break loop90;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e) {
          reportError(e);
          throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end precedenceBitwiseXorExpression

    public static class precedenceStarOperator_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start precedenceStarOperator
    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:648:1: precedenceStarOperator : ( STAR | DIVIDE | MOD );
    public final precedenceStarOperator_return precedenceStarOperator() throws RecognitionException {
        precedenceStarOperator_return retval = new precedenceStarOperator_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token set336=null;

        CommonTree set336_tree=null;

        try {
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:649:5: ( STAR | DIVIDE | MOD )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:
            {
            root_0 = (CommonTree)adaptor.nil();

            set336=(Token)input.LT(1);
            if ( input.LA(1)==STAR||(input.LA(1)>=DIVIDE && input.LA(1)<=MOD) ) {
                input.consume();
                if ( backtracking==0 ) adaptor.addChild(root_0, adaptor.create(set336));
                errorRecovery=false;failed=false;
            }
            else {
                if (backtracking>0) {failed=true; return retval;}
                MismatchedSetException mse =
                    new MismatchedSetException(null,input);
                recoverFromMismatchedSet(input,mse,FOLLOW_set_in_precedenceStarOperator0);    throw mse;
            }


            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e) {
          reportError(e);
          throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end precedenceStarOperator

    public static class precedenceStarExpression_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start precedenceStarExpression
    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:653:1: precedenceStarExpression : precedenceBitwiseXorExpression ( precedenceStarOperator precedenceBitwiseXorExpression )* ;
    public final precedenceStarExpression_return precedenceStarExpression() throws RecognitionException {
        precedenceStarExpression_return retval = new precedenceStarExpression_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        precedenceBitwiseXorExpression_return precedenceBitwiseXorExpression337 = null;

        precedenceStarOperator_return precedenceStarOperator338 = null;

        precedenceBitwiseXorExpression_return precedenceBitwiseXorExpression339 = null;



        try {
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:654:5: ( precedenceBitwiseXorExpression ( precedenceStarOperator precedenceBitwiseXorExpression )* )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:655:5: precedenceBitwiseXorExpression ( precedenceStarOperator precedenceBitwiseXorExpression )*
            {
            root_0 = (CommonTree)adaptor.nil();

            pushFollow(FOLLOW_precedenceBitwiseXorExpression_in_precedenceStarExpression4744);
            precedenceBitwiseXorExpression337=precedenceBitwiseXorExpression();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) adaptor.addChild(root_0, precedenceBitwiseXorExpression337.getTree());
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:655:36: ( precedenceStarOperator precedenceBitwiseXorExpression )*
            loop91:
            do {
                int alt91=2;
                int LA91_0 = input.LA(1);

                if ( (LA91_0==STAR||(LA91_0>=DIVIDE && LA91_0<=MOD)) ) {
                    alt91=1;
                }


                switch (alt91) {
            	case 1 :
            	    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:655:37: precedenceStarOperator precedenceBitwiseXorExpression
            	    {
            	    pushFollow(FOLLOW_precedenceStarOperator_in_precedenceStarExpression4747);
            	    precedenceStarOperator338=precedenceStarOperator();
            	    _fsp--;
            	    if (failed) return retval;
            	    if ( backtracking==0 ) root_0 = (CommonTree)adaptor.becomeRoot(precedenceStarOperator338.getTree(), root_0);
            	    pushFollow(FOLLOW_precedenceBitwiseXorExpression_in_precedenceStarExpression4750);
            	    precedenceBitwiseXorExpression339=precedenceBitwiseXorExpression();
            	    _fsp--;
            	    if (failed) return retval;
            	    if ( backtracking==0 ) adaptor.addChild(root_0, precedenceBitwiseXorExpression339.getTree());

            	    }
            	    break;

            	default :
            	    break loop91;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e) {
          reportError(e);
          throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end precedenceStarExpression

    public static class precedencePlusOperator_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start precedencePlusOperator
    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:659:1: precedencePlusOperator : ( PLUS | MINUS );
    public final precedencePlusOperator_return precedencePlusOperator() throws RecognitionException {
        precedencePlusOperator_return retval = new precedencePlusOperator_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token set340=null;

        CommonTree set340_tree=null;

        try {
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:660:5: ( PLUS | MINUS )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:
            {
            root_0 = (CommonTree)adaptor.nil();

            set340=(Token)input.LT(1);
            if ( (input.LA(1)>=PLUS && input.LA(1)<=MINUS) ) {
                input.consume();
                if ( backtracking==0 ) adaptor.addChild(root_0, adaptor.create(set340));
                errorRecovery=false;failed=false;
            }
            else {
                if (backtracking>0) {failed=true; return retval;}
                MismatchedSetException mse =
                    new MismatchedSetException(null,input);
                recoverFromMismatchedSet(input,mse,FOLLOW_set_in_precedencePlusOperator0);    throw mse;
            }


            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e) {
          reportError(e);
          throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end precedencePlusOperator

    public static class precedencePlusExpression_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start precedencePlusExpression
    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:664:1: precedencePlusExpression : precedenceStarExpression ( precedencePlusOperator precedenceStarExpression )* ;
    public final precedencePlusExpression_return precedencePlusExpression() throws RecognitionException {
        precedencePlusExpression_return retval = new precedencePlusExpression_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        precedenceStarExpression_return precedenceStarExpression341 = null;

        precedencePlusOperator_return precedencePlusOperator342 = null;

        precedenceStarExpression_return precedenceStarExpression343 = null;



        try {
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:665:5: ( precedenceStarExpression ( precedencePlusOperator precedenceStarExpression )* )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:666:5: precedenceStarExpression ( precedencePlusOperator precedenceStarExpression )*
            {
            root_0 = (CommonTree)adaptor.nil();

            pushFollow(FOLLOW_precedenceStarExpression_in_precedencePlusExpression4799);
            precedenceStarExpression341=precedenceStarExpression();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) adaptor.addChild(root_0, precedenceStarExpression341.getTree());
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:666:30: ( precedencePlusOperator precedenceStarExpression )*
            loop92:
            do {
                int alt92=2;
                int LA92_0 = input.LA(1);

                if ( ((LA92_0>=PLUS && LA92_0<=MINUS)) ) {
                    alt92=1;
                }


                switch (alt92) {
            	case 1 :
            	    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:666:31: precedencePlusOperator precedenceStarExpression
            	    {
            	    pushFollow(FOLLOW_precedencePlusOperator_in_precedencePlusExpression4802);
            	    precedencePlusOperator342=precedencePlusOperator();
            	    _fsp--;
            	    if (failed) return retval;
            	    if ( backtracking==0 ) root_0 = (CommonTree)adaptor.becomeRoot(precedencePlusOperator342.getTree(), root_0);
            	    pushFollow(FOLLOW_precedenceStarExpression_in_precedencePlusExpression4805);
            	    precedenceStarExpression343=precedenceStarExpression();
            	    _fsp--;
            	    if (failed) return retval;
            	    if ( backtracking==0 ) adaptor.addChild(root_0, precedenceStarExpression343.getTree());

            	    }
            	    break;

            	default :
            	    break loop92;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e) {
          reportError(e);
          throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end precedencePlusExpression

    public static class precedenceAmpersandOperator_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start precedenceAmpersandOperator
    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:670:1: precedenceAmpersandOperator : AMPERSAND ;
    public final precedenceAmpersandOperator_return precedenceAmpersandOperator() throws RecognitionException {
        precedenceAmpersandOperator_return retval = new precedenceAmpersandOperator_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token AMPERSAND344=null;

        CommonTree AMPERSAND344_tree=null;

        try {
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:671:5: ( AMPERSAND )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:672:5: AMPERSAND
            {
            root_0 = (CommonTree)adaptor.nil();

            AMPERSAND344=(Token)input.LT(1);
            match(input,AMPERSAND,FOLLOW_AMPERSAND_in_precedenceAmpersandOperator4829); if (failed) return retval;
            if ( backtracking==0 ) {
            AMPERSAND344_tree = (CommonTree)adaptor.create(AMPERSAND344);
            adaptor.addChild(root_0, AMPERSAND344_tree);
            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e) {
          reportError(e);
          throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end precedenceAmpersandOperator

    public static class precedenceAmpersandExpression_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start precedenceAmpersandExpression
    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:675:1: precedenceAmpersandExpression : precedencePlusExpression ( precedenceAmpersandOperator precedencePlusExpression )* ;
    public final precedenceAmpersandExpression_return precedenceAmpersandExpression() throws RecognitionException {
        precedenceAmpersandExpression_return retval = new precedenceAmpersandExpression_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        precedencePlusExpression_return precedencePlusExpression345 = null;

        precedenceAmpersandOperator_return precedenceAmpersandOperator346 = null;

        precedencePlusExpression_return precedencePlusExpression347 = null;



        try {
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:676:5: ( precedencePlusExpression ( precedenceAmpersandOperator precedencePlusExpression )* )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:677:5: precedencePlusExpression ( precedenceAmpersandOperator precedencePlusExpression )*
            {
            root_0 = (CommonTree)adaptor.nil();

            pushFollow(FOLLOW_precedencePlusExpression_in_precedenceAmpersandExpression4850);
            precedencePlusExpression345=precedencePlusExpression();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) adaptor.addChild(root_0, precedencePlusExpression345.getTree());
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:677:30: ( precedenceAmpersandOperator precedencePlusExpression )*
            loop93:
            do {
                int alt93=2;
                int LA93_0 = input.LA(1);

                if ( (LA93_0==AMPERSAND) ) {
                    alt93=1;
                }


                switch (alt93) {
            	case 1 :
            	    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:677:31: precedenceAmpersandOperator precedencePlusExpression
            	    {
            	    pushFollow(FOLLOW_precedenceAmpersandOperator_in_precedenceAmpersandExpression4853);
            	    precedenceAmpersandOperator346=precedenceAmpersandOperator();
            	    _fsp--;
            	    if (failed) return retval;
            	    if ( backtracking==0 ) root_0 = (CommonTree)adaptor.becomeRoot(precedenceAmpersandOperator346.getTree(), root_0);
            	    pushFollow(FOLLOW_precedencePlusExpression_in_precedenceAmpersandExpression4856);
            	    precedencePlusExpression347=precedencePlusExpression();
            	    _fsp--;
            	    if (failed) return retval;
            	    if ( backtracking==0 ) adaptor.addChild(root_0, precedencePlusExpression347.getTree());

            	    }
            	    break;

            	default :
            	    break loop93;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e) {
          reportError(e);
          throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end precedenceAmpersandExpression

    public static class precedenceBitwiseOrOperator_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start precedenceBitwiseOrOperator
    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:681:1: precedenceBitwiseOrOperator : BITWISEOR ;
    public final precedenceBitwiseOrOperator_return precedenceBitwiseOrOperator() throws RecognitionException {
        precedenceBitwiseOrOperator_return retval = new precedenceBitwiseOrOperator_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token BITWISEOR348=null;

        CommonTree BITWISEOR348_tree=null;

        try {
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:682:5: ( BITWISEOR )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:683:5: BITWISEOR
            {
            root_0 = (CommonTree)adaptor.nil();

            BITWISEOR348=(Token)input.LT(1);
            match(input,BITWISEOR,FOLLOW_BITWISEOR_in_precedenceBitwiseOrOperator4880); if (failed) return retval;
            if ( backtracking==0 ) {
            BITWISEOR348_tree = (CommonTree)adaptor.create(BITWISEOR348);
            adaptor.addChild(root_0, BITWISEOR348_tree);
            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e) {
          reportError(e);
          throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end precedenceBitwiseOrOperator

    public static class precedenceBitwiseOrExpression_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start precedenceBitwiseOrExpression
    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:686:1: precedenceBitwiseOrExpression : precedenceAmpersandExpression ( precedenceBitwiseOrOperator precedenceAmpersandExpression )* ;
    public final precedenceBitwiseOrExpression_return precedenceBitwiseOrExpression() throws RecognitionException {
        precedenceBitwiseOrExpression_return retval = new precedenceBitwiseOrExpression_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        precedenceAmpersandExpression_return precedenceAmpersandExpression349 = null;

        precedenceBitwiseOrOperator_return precedenceBitwiseOrOperator350 = null;

        precedenceAmpersandExpression_return precedenceAmpersandExpression351 = null;



        try {
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:687:5: ( precedenceAmpersandExpression ( precedenceBitwiseOrOperator precedenceAmpersandExpression )* )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:688:5: precedenceAmpersandExpression ( precedenceBitwiseOrOperator precedenceAmpersandExpression )*
            {
            root_0 = (CommonTree)adaptor.nil();

            pushFollow(FOLLOW_precedenceAmpersandExpression_in_precedenceBitwiseOrExpression4901);
            precedenceAmpersandExpression349=precedenceAmpersandExpression();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) adaptor.addChild(root_0, precedenceAmpersandExpression349.getTree());
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:688:35: ( precedenceBitwiseOrOperator precedenceAmpersandExpression )*
            loop94:
            do {
                int alt94=2;
                int LA94_0 = input.LA(1);

                if ( (LA94_0==BITWISEOR) ) {
                    alt94=1;
                }


                switch (alt94) {
            	case 1 :
            	    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:688:36: precedenceBitwiseOrOperator precedenceAmpersandExpression
            	    {
            	    pushFollow(FOLLOW_precedenceBitwiseOrOperator_in_precedenceBitwiseOrExpression4904);
            	    precedenceBitwiseOrOperator350=precedenceBitwiseOrOperator();
            	    _fsp--;
            	    if (failed) return retval;
            	    if ( backtracking==0 ) root_0 = (CommonTree)adaptor.becomeRoot(precedenceBitwiseOrOperator350.getTree(), root_0);
            	    pushFollow(FOLLOW_precedenceAmpersandExpression_in_precedenceBitwiseOrExpression4907);
            	    precedenceAmpersandExpression351=precedenceAmpersandExpression();
            	    _fsp--;
            	    if (failed) return retval;
            	    if ( backtracking==0 ) adaptor.addChild(root_0, precedenceAmpersandExpression351.getTree());

            	    }
            	    break;

            	default :
            	    break loop94;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e) {
          reportError(e);
          throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end precedenceBitwiseOrExpression

    public static class precedenceEqualOperator_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start precedenceEqualOperator
    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:692:1: precedenceEqualOperator : ( EQUAL | NOTEQUAL | LESSTHANOREQUALTO | LESSTHAN | GREATERTHANOREQUALTO | GREATERTHAN | KW_LIKE | KW_RLIKE | KW_REGEXP );
    public final precedenceEqualOperator_return precedenceEqualOperator() throws RecognitionException {
        precedenceEqualOperator_return retval = new precedenceEqualOperator_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token set352=null;

        CommonTree set352_tree=null;

        try {
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:693:5: ( EQUAL | NOTEQUAL | LESSTHANOREQUALTO | LESSTHAN | GREATERTHANOREQUALTO | GREATERTHAN | KW_LIKE | KW_RLIKE | KW_REGEXP )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:
            {
            root_0 = (CommonTree)adaptor.nil();

            set352=(Token)input.LT(1);
            if ( input.LA(1)==EQUAL||(input.LA(1)>=LESSTHAN && input.LA(1)<=GREATERTHAN)||(input.LA(1)>=NOTEQUAL && input.LA(1)<=KW_REGEXP) ) {
                input.consume();
                if ( backtracking==0 ) adaptor.addChild(root_0, adaptor.create(set352));
                errorRecovery=false;failed=false;
            }
            else {
                if (backtracking>0) {failed=true; return retval;}
                MismatchedSetException mse =
                    new MismatchedSetException(null,input);
                recoverFromMismatchedSet(input,mse,FOLLOW_set_in_precedenceEqualOperator0);    throw mse;
            }


            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e) {
          reportError(e);
          throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end precedenceEqualOperator

    public static class precedenceEqualExpression_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start precedenceEqualExpression
    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:698:1: precedenceEqualExpression : precedenceBitwiseOrExpression ( precedenceEqualOperator precedenceBitwiseOrExpression )* ;
    public final precedenceEqualExpression_return precedenceEqualExpression() throws RecognitionException {
        precedenceEqualExpression_return retval = new precedenceEqualExpression_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        precedenceBitwiseOrExpression_return precedenceBitwiseOrExpression353 = null;

        precedenceEqualOperator_return precedenceEqualOperator354 = null;

        precedenceBitwiseOrExpression_return precedenceBitwiseOrExpression355 = null;



        try {
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:699:5: ( precedenceBitwiseOrExpression ( precedenceEqualOperator precedenceBitwiseOrExpression )* )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:700:5: precedenceBitwiseOrExpression ( precedenceEqualOperator precedenceBitwiseOrExpression )*
            {
            root_0 = (CommonTree)adaptor.nil();

            pushFollow(FOLLOW_precedenceBitwiseOrExpression_in_precedenceEqualExpression4988);
            precedenceBitwiseOrExpression353=precedenceBitwiseOrExpression();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) adaptor.addChild(root_0, precedenceBitwiseOrExpression353.getTree());
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:700:35: ( precedenceEqualOperator precedenceBitwiseOrExpression )*
            loop95:
            do {
                int alt95=2;
                int LA95_0 = input.LA(1);

                if ( (LA95_0==EQUAL||(LA95_0>=LESSTHAN && LA95_0<=GREATERTHAN)||(LA95_0>=NOTEQUAL && LA95_0<=KW_REGEXP)) ) {
                    alt95=1;
                }


                switch (alt95) {
            	case 1 :
            	    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:700:36: precedenceEqualOperator precedenceBitwiseOrExpression
            	    {
            	    pushFollow(FOLLOW_precedenceEqualOperator_in_precedenceEqualExpression4991);
            	    precedenceEqualOperator354=precedenceEqualOperator();
            	    _fsp--;
            	    if (failed) return retval;
            	    if ( backtracking==0 ) root_0 = (CommonTree)adaptor.becomeRoot(precedenceEqualOperator354.getTree(), root_0);
            	    pushFollow(FOLLOW_precedenceBitwiseOrExpression_in_precedenceEqualExpression4994);
            	    precedenceBitwiseOrExpression355=precedenceBitwiseOrExpression();
            	    _fsp--;
            	    if (failed) return retval;
            	    if ( backtracking==0 ) adaptor.addChild(root_0, precedenceBitwiseOrExpression355.getTree());

            	    }
            	    break;

            	default :
            	    break loop95;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e) {
          reportError(e);
          throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end precedenceEqualExpression

    public static class precedenceNotOperator_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start precedenceNotOperator
    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:704:1: precedenceNotOperator : KW_NOT ;
    public final precedenceNotOperator_return precedenceNotOperator() throws RecognitionException {
        precedenceNotOperator_return retval = new precedenceNotOperator_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token KW_NOT356=null;

        CommonTree KW_NOT356_tree=null;

        try {
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:705:5: ( KW_NOT )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:706:5: KW_NOT
            {
            root_0 = (CommonTree)adaptor.nil();

            KW_NOT356=(Token)input.LT(1);
            match(input,KW_NOT,FOLLOW_KW_NOT_in_precedenceNotOperator5018); if (failed) return retval;
            if ( backtracking==0 ) {
            KW_NOT356_tree = (CommonTree)adaptor.create(KW_NOT356);
            adaptor.addChild(root_0, KW_NOT356_tree);
            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e) {
          reportError(e);
          throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end precedenceNotOperator

    public static class precedenceNotExpression_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start precedenceNotExpression
    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:709:1: precedenceNotExpression : ( precedenceNotOperator )* precedenceEqualExpression ;
    public final precedenceNotExpression_return precedenceNotExpression() throws RecognitionException {
        precedenceNotExpression_return retval = new precedenceNotExpression_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        precedenceNotOperator_return precedenceNotOperator357 = null;

        precedenceEqualExpression_return precedenceEqualExpression358 = null;



        try {
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:710:5: ( ( precedenceNotOperator )* precedenceEqualExpression )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:711:5: ( precedenceNotOperator )* precedenceEqualExpression
            {
            root_0 = (CommonTree)adaptor.nil();

            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:711:5: ( precedenceNotOperator )*
            loop96:
            do {
                int alt96=2;
                int LA96_0 = input.LA(1);

                if ( (LA96_0==KW_NOT) ) {
                    alt96=1;
                }


                switch (alt96) {
            	case 1 :
            	    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:711:6: precedenceNotOperator
            	    {
            	    pushFollow(FOLLOW_precedenceNotOperator_in_precedenceNotExpression5040);
            	    precedenceNotOperator357=precedenceNotOperator();
            	    _fsp--;
            	    if (failed) return retval;
            	    if ( backtracking==0 ) root_0 = (CommonTree)adaptor.becomeRoot(precedenceNotOperator357.getTree(), root_0);

            	    }
            	    break;

            	default :
            	    break loop96;
                }
            } while (true);

            pushFollow(FOLLOW_precedenceEqualExpression_in_precedenceNotExpression5045);
            precedenceEqualExpression358=precedenceEqualExpression();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) adaptor.addChild(root_0, precedenceEqualExpression358.getTree());

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e) {
          reportError(e);
          throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end precedenceNotExpression

    public static class precedenceAndOperator_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start precedenceAndOperator
    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:715:1: precedenceAndOperator : KW_AND ;
    public final precedenceAndOperator_return precedenceAndOperator() throws RecognitionException {
        precedenceAndOperator_return retval = new precedenceAndOperator_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token KW_AND359=null;

        CommonTree KW_AND359_tree=null;

        try {
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:716:5: ( KW_AND )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:717:5: KW_AND
            {
            root_0 = (CommonTree)adaptor.nil();

            KW_AND359=(Token)input.LT(1);
            match(input,KW_AND,FOLLOW_KW_AND_in_precedenceAndOperator5067); if (failed) return retval;
            if ( backtracking==0 ) {
            KW_AND359_tree = (CommonTree)adaptor.create(KW_AND359);
            adaptor.addChild(root_0, KW_AND359_tree);
            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e) {
          reportError(e);
          throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end precedenceAndOperator

    public static class precedenceAndExpression_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start precedenceAndExpression
    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:720:1: precedenceAndExpression : precedenceNotExpression ( precedenceAndOperator precedenceNotExpression )* ;
    public final precedenceAndExpression_return precedenceAndExpression() throws RecognitionException {
        precedenceAndExpression_return retval = new precedenceAndExpression_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        precedenceNotExpression_return precedenceNotExpression360 = null;

        precedenceAndOperator_return precedenceAndOperator361 = null;

        precedenceNotExpression_return precedenceNotExpression362 = null;



        try {
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:721:5: ( precedenceNotExpression ( precedenceAndOperator precedenceNotExpression )* )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:722:5: precedenceNotExpression ( precedenceAndOperator precedenceNotExpression )*
            {
            root_0 = (CommonTree)adaptor.nil();

            pushFollow(FOLLOW_precedenceNotExpression_in_precedenceAndExpression5088);
            precedenceNotExpression360=precedenceNotExpression();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) adaptor.addChild(root_0, precedenceNotExpression360.getTree());
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:722:29: ( precedenceAndOperator precedenceNotExpression )*
            loop97:
            do {
                int alt97=2;
                int LA97_0 = input.LA(1);

                if ( (LA97_0==KW_AND) ) {
                    alt97=1;
                }


                switch (alt97) {
            	case 1 :
            	    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:722:30: precedenceAndOperator precedenceNotExpression
            	    {
            	    pushFollow(FOLLOW_precedenceAndOperator_in_precedenceAndExpression5091);
            	    precedenceAndOperator361=precedenceAndOperator();
            	    _fsp--;
            	    if (failed) return retval;
            	    if ( backtracking==0 ) root_0 = (CommonTree)adaptor.becomeRoot(precedenceAndOperator361.getTree(), root_0);
            	    pushFollow(FOLLOW_precedenceNotExpression_in_precedenceAndExpression5094);
            	    precedenceNotExpression362=precedenceNotExpression();
            	    _fsp--;
            	    if (failed) return retval;
            	    if ( backtracking==0 ) adaptor.addChild(root_0, precedenceNotExpression362.getTree());

            	    }
            	    break;

            	default :
            	    break loop97;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e) {
          reportError(e);
          throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end precedenceAndExpression

    public static class precedenceOrOperator_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start precedenceOrOperator
    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:726:1: precedenceOrOperator : KW_OR ;
    public final precedenceOrOperator_return precedenceOrOperator() throws RecognitionException {
        precedenceOrOperator_return retval = new precedenceOrOperator_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token KW_OR363=null;

        CommonTree KW_OR363_tree=null;

        try {
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:727:5: ( KW_OR )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:728:5: KW_OR
            {
            root_0 = (CommonTree)adaptor.nil();

            KW_OR363=(Token)input.LT(1);
            match(input,KW_OR,FOLLOW_KW_OR_in_precedenceOrOperator5118); if (failed) return retval;
            if ( backtracking==0 ) {
            KW_OR363_tree = (CommonTree)adaptor.create(KW_OR363);
            adaptor.addChild(root_0, KW_OR363_tree);
            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e) {
          reportError(e);
          throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end precedenceOrOperator

    public static class precedenceOrExpression_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start precedenceOrExpression
    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:731:1: precedenceOrExpression : precedenceAndExpression ( precedenceOrOperator precedenceAndExpression )* ;
    public final precedenceOrExpression_return precedenceOrExpression() throws RecognitionException {
        precedenceOrExpression_return retval = new precedenceOrExpression_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        precedenceAndExpression_return precedenceAndExpression364 = null;

        precedenceOrOperator_return precedenceOrOperator365 = null;

        precedenceAndExpression_return precedenceAndExpression366 = null;



        try {
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:732:5: ( precedenceAndExpression ( precedenceOrOperator precedenceAndExpression )* )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:733:5: precedenceAndExpression ( precedenceOrOperator precedenceAndExpression )*
            {
            root_0 = (CommonTree)adaptor.nil();

            pushFollow(FOLLOW_precedenceAndExpression_in_precedenceOrExpression5139);
            precedenceAndExpression364=precedenceAndExpression();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) adaptor.addChild(root_0, precedenceAndExpression364.getTree());
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:733:29: ( precedenceOrOperator precedenceAndExpression )*
            loop98:
            do {
                int alt98=2;
                int LA98_0 = input.LA(1);

                if ( (LA98_0==KW_OR) ) {
                    alt98=1;
                }


                switch (alt98) {
            	case 1 :
            	    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:733:30: precedenceOrOperator precedenceAndExpression
            	    {
            	    pushFollow(FOLLOW_precedenceOrOperator_in_precedenceOrExpression5142);
            	    precedenceOrOperator365=precedenceOrOperator();
            	    _fsp--;
            	    if (failed) return retval;
            	    if ( backtracking==0 ) root_0 = (CommonTree)adaptor.becomeRoot(precedenceOrOperator365.getTree(), root_0);
            	    pushFollow(FOLLOW_precedenceAndExpression_in_precedenceOrExpression5145);
            	    precedenceAndExpression366=precedenceAndExpression();
            	    _fsp--;
            	    if (failed) return retval;
            	    if ( backtracking==0 ) adaptor.addChild(root_0, precedenceAndExpression366.getTree());

            	    }
            	    break;

            	default :
            	    break loop98;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e) {
          reportError(e);
          throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end precedenceOrExpression

    public static class booleanValue_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start booleanValue
    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:737:1: booleanValue : ( KW_TRUE | KW_FALSE );
    public final booleanValue_return booleanValue() throws RecognitionException {
        booleanValue_return retval = new booleanValue_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token KW_TRUE367=null;
        Token KW_FALSE368=null;

        CommonTree KW_TRUE367_tree=null;
        CommonTree KW_FALSE368_tree=null;

        try {
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:738:5: ( KW_TRUE | KW_FALSE )
            int alt99=2;
            int LA99_0 = input.LA(1);

            if ( (LA99_0==KW_TRUE) ) {
                alt99=1;
            }
            else if ( (LA99_0==KW_FALSE) ) {
                alt99=2;
            }
            else {
                if (backtracking>0) {failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("737:1: booleanValue : ( KW_TRUE | KW_FALSE );", 99, 0, input);

                throw nvae;
            }
            switch (alt99) {
                case 1 :
                    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:739:5: KW_TRUE
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    KW_TRUE367=(Token)input.LT(1);
                    match(input,KW_TRUE,FOLLOW_KW_TRUE_in_booleanValue5169); if (failed) return retval;
                    if ( backtracking==0 ) {
                    KW_TRUE367_tree = (CommonTree)adaptor.create(KW_TRUE367);
                    root_0 = (CommonTree)adaptor.becomeRoot(KW_TRUE367_tree, root_0);
                    }

                    }
                    break;
                case 2 :
                    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:739:16: KW_FALSE
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    KW_FALSE368=(Token)input.LT(1);
                    match(input,KW_FALSE,FOLLOW_KW_FALSE_in_booleanValue5174); if (failed) return retval;
                    if ( backtracking==0 ) {
                    KW_FALSE368_tree = (CommonTree)adaptor.create(KW_FALSE368);
                    root_0 = (CommonTree)adaptor.becomeRoot(KW_FALSE368_tree, root_0);
                    }

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e) {
          reportError(e);
          throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end booleanValue

    public static class tabName_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start tabName
    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:742:1: tabName : Identifier ( partitionSpec )? -> ^( TOK_TAB Identifier ( partitionSpec )? ) ;
    public final tabName_return tabName() throws RecognitionException {
        tabName_return retval = new tabName_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token Identifier369=null;
        partitionSpec_return partitionSpec370 = null;


        CommonTree Identifier369_tree=null;
        RewriteRuleTokenStream stream_Identifier=new RewriteRuleTokenStream(adaptor,"token Identifier");
        RewriteRuleSubtreeStream stream_partitionSpec=new RewriteRuleSubtreeStream(adaptor,"rule partitionSpec");
        try {
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:743:4: ( Identifier ( partitionSpec )? -> ^( TOK_TAB Identifier ( partitionSpec )? ) )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:744:4: Identifier ( partitionSpec )?
            {
            Identifier369=(Token)input.LT(1);
            match(input,Identifier,FOLLOW_Identifier_in_tabName5194); if (failed) return retval;
            if ( backtracking==0 ) stream_Identifier.add(Identifier369);

            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:744:15: ( partitionSpec )?
            int alt100=2;
            int LA100_0 = input.LA(1);

            if ( (LA100_0==KW_PARTITION) ) {
                alt100=1;
            }
            switch (alt100) {
                case 1 :
                    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:0:0: partitionSpec
                    {
                    pushFollow(FOLLOW_partitionSpec_in_tabName5196);
                    partitionSpec370=partitionSpec();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_partitionSpec.add(partitionSpec370.getTree());

                    }
                    break;

            }


            // AST REWRITE
            // elements: partitionSpec, Identifier
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 744:30: -> ^( TOK_TAB Identifier ( partitionSpec )? )
            {
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:744:33: ^( TOK_TAB Identifier ( partitionSpec )? )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(adaptor.create(TOK_TAB, "TOK_TAB"), root_1);

                adaptor.addChild(root_1, stream_Identifier.next());
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:744:54: ( partitionSpec )?
                if ( stream_partitionSpec.hasNext() ) {
                    adaptor.addChild(root_1, stream_partitionSpec.next());

                }
                stream_partitionSpec.reset();

                adaptor.addChild(root_0, root_1);
                }

            }

            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e) {
          reportError(e);
          throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end tabName

    public static class partitionSpec_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start partitionSpec
    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:747:1: partitionSpec : KW_PARTITION LPAREN partitionVal ( COMMA partitionVal )* RPAREN -> ^( TOK_PARTSPEC ( partitionVal )+ ) ;
    public final partitionSpec_return partitionSpec() throws RecognitionException {
        partitionSpec_return retval = new partitionSpec_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token KW_PARTITION371=null;
        Token LPAREN372=null;
        Token COMMA374=null;
        Token RPAREN376=null;
        partitionVal_return partitionVal373 = null;

        partitionVal_return partitionVal375 = null;


        CommonTree KW_PARTITION371_tree=null;
        CommonTree LPAREN372_tree=null;
        CommonTree COMMA374_tree=null;
        CommonTree RPAREN376_tree=null;
        RewriteRuleTokenStream stream_RPAREN=new RewriteRuleTokenStream(adaptor,"token RPAREN");
        RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
        RewriteRuleTokenStream stream_KW_PARTITION=new RewriteRuleTokenStream(adaptor,"token KW_PARTITION");
        RewriteRuleTokenStream stream_LPAREN=new RewriteRuleTokenStream(adaptor,"token LPAREN");
        RewriteRuleSubtreeStream stream_partitionVal=new RewriteRuleSubtreeStream(adaptor,"rule partitionVal");
        try {
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:748:5: ( KW_PARTITION LPAREN partitionVal ( COMMA partitionVal )* RPAREN -> ^( TOK_PARTSPEC ( partitionVal )+ ) )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:749:5: KW_PARTITION LPAREN partitionVal ( COMMA partitionVal )* RPAREN
            {
            KW_PARTITION371=(Token)input.LT(1);
            match(input,KW_PARTITION,FOLLOW_KW_PARTITION_in_partitionSpec5236); if (failed) return retval;
            if ( backtracking==0 ) stream_KW_PARTITION.add(KW_PARTITION371);

            LPAREN372=(Token)input.LT(1);
            match(input,LPAREN,FOLLOW_LPAREN_in_partitionSpec5243); if (failed) return retval;
            if ( backtracking==0 ) stream_LPAREN.add(LPAREN372);

            pushFollow(FOLLOW_partitionVal_in_partitionSpec5245);
            partitionVal373=partitionVal();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_partitionVal.add(partitionVal373.getTree());
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:750:26: ( COMMA partitionVal )*
            loop101:
            do {
                int alt101=2;
                int LA101_0 = input.LA(1);

                if ( (LA101_0==COMMA) ) {
                    alt101=1;
                }


                switch (alt101) {
            	case 1 :
            	    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:750:27: COMMA partitionVal
            	    {
            	    COMMA374=(Token)input.LT(1);
            	    match(input,COMMA,FOLLOW_COMMA_in_partitionSpec5248); if (failed) return retval;
            	    if ( backtracking==0 ) stream_COMMA.add(COMMA374);

            	    pushFollow(FOLLOW_partitionVal_in_partitionSpec5251);
            	    partitionVal375=partitionVal();
            	    _fsp--;
            	    if (failed) return retval;
            	    if ( backtracking==0 ) stream_partitionVal.add(partitionVal375.getTree());

            	    }
            	    break;

            	default :
            	    break loop101;
                }
            } while (true);

            RPAREN376=(Token)input.LT(1);
            match(input,RPAREN,FOLLOW_RPAREN_in_partitionSpec5256); if (failed) return retval;
            if ( backtracking==0 ) stream_RPAREN.add(RPAREN376);


            // AST REWRITE
            // elements: partitionVal
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 750:57: -> ^( TOK_PARTSPEC ( partitionVal )+ )
            {
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:750:60: ^( TOK_PARTSPEC ( partitionVal )+ )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(adaptor.create(TOK_PARTSPEC, "TOK_PARTSPEC"), root_1);

                if ( !(stream_partitionVal.hasNext()) ) {
                    throw new RewriteEarlyExitException();
                }
                while ( stream_partitionVal.hasNext() ) {
                    adaptor.addChild(root_1, stream_partitionVal.next());

                }
                stream_partitionVal.reset();

                adaptor.addChild(root_0, root_1);
                }

            }

            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e) {
          reportError(e);
          throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end partitionSpec

    public static class partitionVal_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start partitionVal
    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:753:1: partitionVal : Identifier EQUAL constant -> ^( TOK_PARTVAL Identifier constant ) ;
    public final partitionVal_return partitionVal() throws RecognitionException {
        partitionVal_return retval = new partitionVal_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token Identifier377=null;
        Token EQUAL378=null;
        constant_return constant379 = null;


        CommonTree Identifier377_tree=null;
        CommonTree EQUAL378_tree=null;
        RewriteRuleTokenStream stream_Identifier=new RewriteRuleTokenStream(adaptor,"token Identifier");
        RewriteRuleTokenStream stream_EQUAL=new RewriteRuleTokenStream(adaptor,"token EQUAL");
        RewriteRuleSubtreeStream stream_constant=new RewriteRuleSubtreeStream(adaptor,"rule constant");
        try {
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:754:5: ( Identifier EQUAL constant -> ^( TOK_PARTVAL Identifier constant ) )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:755:5: Identifier EQUAL constant
            {
            Identifier377=(Token)input.LT(1);
            match(input,Identifier,FOLLOW_Identifier_in_partitionVal5287); if (failed) return retval;
            if ( backtracking==0 ) stream_Identifier.add(Identifier377);

            EQUAL378=(Token)input.LT(1);
            match(input,EQUAL,FOLLOW_EQUAL_in_partitionVal5289); if (failed) return retval;
            if ( backtracking==0 ) stream_EQUAL.add(EQUAL378);

            pushFollow(FOLLOW_constant_in_partitionVal5291);
            constant379=constant();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_constant.add(constant379.getTree());

            // AST REWRITE
            // elements: Identifier, constant
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 755:31: -> ^( TOK_PARTVAL Identifier constant )
            {
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:755:34: ^( TOK_PARTVAL Identifier constant )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(adaptor.create(TOK_PARTVAL, "TOK_PARTVAL"), root_1);

                adaptor.addChild(root_1, stream_Identifier.next());
                adaptor.addChild(root_1, stream_constant.next());

                adaptor.addChild(root_0, root_1);
                }

            }

            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e) {
          reportError(e);
          throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end partitionVal

    // $ANTLR start synpred52
    public final void synpred52_fragment() throws RecognitionException {   
        // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:346:23: ( queryOperator queryStatementExpression )
        // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:346:23: queryOperator queryStatementExpression
        {
        pushFollow(FOLLOW_queryOperator_in_synpred522415);
        queryOperator();
        _fsp--;
        if (failed) return ;
        pushFollow(FOLLOW_queryStatementExpression_in_synpred522418);
        queryStatementExpression();
        _fsp--;
        if (failed) return ;

        }
    }
    // $ANTLR end synpred52

    // $ANTLR start synpred88
    public final void synpred88_fragment() throws RecognitionException {   
        // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:474:7: ( KW_FROM joinSource )
        // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:474:7: KW_FROM joinSource
        {
        match(input,KW_FROM,FOLLOW_KW_FROM_in_synpred883412); if (failed) return ;
        pushFollow(FOLLOW_joinSource_in_synpred883414);
        joinSource();
        _fsp--;
        if (failed) return ;

        }
    }
    // $ANTLR end synpred88

    // $ANTLR start synpred119
    public final void synpred119_fragment() throws RecognitionException {   
        // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:631:7: ( precedenceFieldExpression KW_IS KW_NULL )
        // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:631:7: precedenceFieldExpression KW_IS KW_NULL
        {
        pushFollow(FOLLOW_precedenceFieldExpression_in_synpred1194590);
        precedenceFieldExpression();
        _fsp--;
        if (failed) return ;
        match(input,KW_IS,FOLLOW_KW_IS_in_synpred1194592); if (failed) return ;
        match(input,KW_NULL,FOLLOW_KW_NULL_in_synpred1194594); if (failed) return ;

        }
    }
    // $ANTLR end synpred119

    // $ANTLR start synpred120
    public final void synpred120_fragment() throws RecognitionException {   
        // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:632:7: ( precedenceFieldExpression KW_IS KW_NOT KW_NULL )
        // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:632:7: precedenceFieldExpression KW_IS KW_NOT KW_NULL
        {
        pushFollow(FOLLOW_precedenceFieldExpression_in_synpred1204612);
        precedenceFieldExpression();
        _fsp--;
        if (failed) return ;
        match(input,KW_IS,FOLLOW_KW_IS_in_synpred1204614); if (failed) return ;
        match(input,KW_NOT,FOLLOW_KW_NOT_in_synpred1204616); if (failed) return ;
        match(input,KW_NULL,FOLLOW_KW_NULL_in_synpred1204618); if (failed) return ;

        }
    }
    // $ANTLR end synpred120

    public final boolean synpred88() {
        backtracking++;
        int start = input.mark();
        try {
            synpred88_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !failed;
        input.rewind(start);
        backtracking--;
        failed=false;
        return success;
    }
    public final boolean synpred52() {
        backtracking++;
        int start = input.mark();
        try {
            synpred52_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !failed;
        input.rewind(start);
        backtracking--;
        failed=false;
        return success;
    }
    public final boolean synpred120() {
        backtracking++;
        int start = input.mark();
        try {
            synpred120_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !failed;
        input.rewind(start);
        backtracking--;
        failed=false;
        return success;
    }
    public final boolean synpred119() {
        backtracking++;
        int start = input.mark();
        try {
            synpred119_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !failed;
        input.rewind(start);
        backtracking--;
        failed=false;
        return success;
    }


 

    public static final BitSet FOLLOW_explainStatement_in_statement367 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_statement369 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_execStatement_in_statement374 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_statement376 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_KW_EXPLAIN_in_explainStatement387 = new BitSet(new long[]{0x0000000000000000L,0x0610180000000000L,0x0412000000000006L});
    public static final BitSet FOLLOW_KW_EXTENDED_in_explainStatement392 = new BitSet(new long[]{0x0000000000000000L,0x0610100000000000L,0x0412000000000006L});
    public static final BitSet FOLLOW_execStatement_in_explainStatement396 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_queryStatementExpression_in_execStatement424 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_loadStatement_in_execStatement432 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ddlStatement_in_execStatement440 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_KW_LOAD_in_loadStatement457 = new BitSet(new long[]{0x0000000000000000L,0x0000200000000000L});
    public static final BitSet FOLLOW_KW_DATA_in_loadStatement459 = new BitSet(new long[]{0x0000000000000000L,0x0000C00000000000L});
    public static final BitSet FOLLOW_KW_LOCAL_in_loadStatement464 = new BitSet(new long[]{0x0000000000000000L,0x0000800000000000L});
    public static final BitSet FOLLOW_KW_INPATH_in_loadStatement468 = new BitSet(new long[]{0x0000000000000000L,0x0001000000000000L});
    public static final BitSet FOLLOW_StringLiteral_in_loadStatement473 = new BitSet(new long[]{0x0000000000000000L,0x0006000000000000L});
    public static final BitSet FOLLOW_KW_OVERWRITE_in_loadStatement479 = new BitSet(new long[]{0x0000000000000000L,0x0004000000000000L});
    public static final BitSet FOLLOW_KW_INTO_in_loadStatement483 = new BitSet(new long[]{0x0000000000000000L,0x0008000000000000L});
    public static final BitSet FOLLOW_KW_TABLE_in_loadStatement485 = new BitSet(new long[]{0x0000000000000000L,0x0040000000000000L});
    public static final BitSet FOLLOW_tabName_in_loadStatement490 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_createStatement_in_ddlStatement533 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_dropStatement_in_ddlStatement541 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_alterStatement_in_ddlStatement549 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_descStatement_in_ddlStatement557 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_showStatement_in_ddlStatement565 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_createFunctionStatement_in_ddlStatement573 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_KW_CREATE_in_createStatement590 = new BitSet(new long[]{0x0000000000000000L,0x0028000000000000L});
    public static final BitSet FOLLOW_KW_EXTERNAL_in_createStatement595 = new BitSet(new long[]{0x0000000000000000L,0x0008000000000000L});
    public static final BitSet FOLLOW_KW_TABLE_in_createStatement599 = new BitSet(new long[]{0x0000000000000000L,0x0040000000000000L});
    public static final BitSet FOLLOW_Identifier_in_createStatement603 = new BitSet(new long[]{0x0000000000000000L,0x0080000000000000L});
    public static final BitSet FOLLOW_LPAREN_in_createStatement605 = new BitSet(new long[]{0x0000000000000000L,0x0040000000000000L});
    public static final BitSet FOLLOW_columnNameTypeList_in_createStatement607 = new BitSet(new long[]{0x0000000000000000L,0x0100000000000000L});
    public static final BitSet FOLLOW_RPAREN_in_createStatement609 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x00000000A0008B00L});
    public static final BitSet FOLLOW_tableComment_in_createStatement611 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x00000000A0008A00L});
    public static final BitSet FOLLOW_tablePartition_in_createStatement614 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x00000000A0008800L});
    public static final BitSet FOLLOW_tableBuckets_in_createStatement617 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x00000000A0008000L});
    public static final BitSet FOLLOW_tableRowFormat_in_createStatement620 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x00000000A0000000L});
    public static final BitSet FOLLOW_tableFileFormat_in_createStatement623 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000000080000000L});
    public static final BitSet FOLLOW_tableLocation_in_createStatement626 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_KW_DROP_in_dropStatement728 = new BitSet(new long[]{0x0000000000000000L,0x0008000000000000L});
    public static final BitSet FOLLOW_KW_TABLE_in_dropStatement730 = new BitSet(new long[]{0x0000000000000000L,0x0040000000000000L});
    public static final BitSet FOLLOW_Identifier_in_dropStatement732 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_alterStatementRename_in_alterStatement758 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_alterStatementAddCol_in_alterStatement766 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_alterStatementDropPartitions_in_alterStatement774 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_KW_ALTER_in_alterStatementRename791 = new BitSet(new long[]{0x0000000000000000L,0x0008000000000000L});
    public static final BitSet FOLLOW_KW_TABLE_in_alterStatementRename793 = new BitSet(new long[]{0x0000000000000000L,0x0040000000000000L});
    public static final BitSet FOLLOW_Identifier_in_alterStatementRename797 = new BitSet(new long[]{0x0000000000000000L,0x0800000000000000L});
    public static final BitSet FOLLOW_KW_RENAME_in_alterStatementRename799 = new BitSet(new long[]{0x0000000000000000L,0x1000000000000000L});
    public static final BitSet FOLLOW_KW_TO_in_alterStatementRename801 = new BitSet(new long[]{0x0000000000000000L,0x0040000000000000L});
    public static final BitSet FOLLOW_Identifier_in_alterStatementRename805 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_KW_ALTER_in_alterStatementAddCol839 = new BitSet(new long[]{0x0000000000000000L,0x0008000000000000L});
    public static final BitSet FOLLOW_KW_TABLE_in_alterStatementAddCol841 = new BitSet(new long[]{0x0000000000000000L,0x0040000000000000L});
    public static final BitSet FOLLOW_Identifier_in_alterStatementAddCol843 = new BitSet(new long[]{0x0000000000000000L,0x6000000000000000L});
    public static final BitSet FOLLOW_KW_ADD_in_alterStatementAddCol848 = new BitSet(new long[]{0x0000000000000000L,0x8000000000000000L});
    public static final BitSet FOLLOW_KW_REPLACE_in_alterStatementAddCol854 = new BitSet(new long[]{0x0000000000000000L,0x8000000000000000L});
    public static final BitSet FOLLOW_KW_COLUMNS_in_alterStatementAddCol857 = new BitSet(new long[]{0x0000000000000000L,0x0080000000000000L});
    public static final BitSet FOLLOW_LPAREN_in_alterStatementAddCol859 = new BitSet(new long[]{0x0000000000000000L,0x0040000000000000L});
    public static final BitSet FOLLOW_columnNameTypeList_in_alterStatementAddCol861 = new BitSet(new long[]{0x0000000000000000L,0x0100000000000000L});
    public static final BitSet FOLLOW_RPAREN_in_alterStatementAddCol863 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_KW_ALTER_in_alterStatementDropPartitions926 = new BitSet(new long[]{0x0000000000000000L,0x0008000000000000L});
    public static final BitSet FOLLOW_KW_TABLE_in_alterStatementDropPartitions928 = new BitSet(new long[]{0x0000000000000000L,0x0040000000000000L});
    public static final BitSet FOLLOW_Identifier_in_alterStatementDropPartitions930 = new BitSet(new long[]{0x0000000000000000L,0x0200000000000000L});
    public static final BitSet FOLLOW_KW_DROP_in_alterStatementDropPartitions932 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000800000000L});
    public static final BitSet FOLLOW_partitionSpec_in_alterStatementDropPartitions934 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_COMMA_in_alterStatementDropPartitions937 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000800000000L});
    public static final BitSet FOLLOW_partitionSpec_in_alterStatementDropPartitions939 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_KW_DESCRIBE_in_descStatement973 = new BitSet(new long[]{0x0000000000000000L,0x0040080000000000L});
    public static final BitSet FOLLOW_KW_EXTENDED_in_descStatement978 = new BitSet(new long[]{0x0000000000000000L,0x0040000000000000L});
    public static final BitSet FOLLOW_tabName_in_descStatement985 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_KW_SHOW_in_showStatement1017 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_KW_TABLES_in_showStatement1019 = new BitSet(new long[]{0x0000000000000002L,0x0041000000000000L});
    public static final BitSet FOLLOW_showStmtIdentifier_in_showStatement1021 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_KW_SHOW_in_showStatement1040 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000010L});
    public static final BitSet FOLLOW_KW_PARTITIONS_in_showStatement1042 = new BitSet(new long[]{0x0000000000000000L,0x0040000000000000L});
    public static final BitSet FOLLOW_Identifier_in_showStatement1044 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_KW_CREATE_in_createFunctionStatement1073 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_KW_TEMPORARY_in_createFunctionStatement1075 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000040L});
    public static final BitSet FOLLOW_KW_FUNCTION_in_createFunctionStatement1077 = new BitSet(new long[]{0x0000000000000000L,0x0040000000000000L});
    public static final BitSet FOLLOW_Identifier_in_createFunctionStatement1079 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000080L});
    public static final BitSet FOLLOW_KW_AS_in_createFunctionStatement1081 = new BitSet(new long[]{0x0000000000000000L,0x0001000000000000L});
    public static final BitSet FOLLOW_StringLiteral_in_createFunctionStatement1083 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_showStmtIdentifier0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_KW_COMMENT_in_tableComment1145 = new BitSet(new long[]{0x0000000000000000L,0x0001000000000000L});
    public static final BitSet FOLLOW_StringLiteral_in_tableComment1149 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_KW_PARTITIONED_in_tablePartition1176 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000400L});
    public static final BitSet FOLLOW_KW_BY_in_tablePartition1178 = new BitSet(new long[]{0x0000000000000000L,0x0080000000000000L});
    public static final BitSet FOLLOW_LPAREN_in_tablePartition1180 = new BitSet(new long[]{0x0000000000000000L,0x0040000000000000L});
    public static final BitSet FOLLOW_columnNameTypeList_in_tablePartition1182 = new BitSet(new long[]{0x0000000000000000L,0x0100000000000000L});
    public static final BitSet FOLLOW_RPAREN_in_tablePartition1184 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_KW_CLUSTERED_in_tableBuckets1220 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000400L});
    public static final BitSet FOLLOW_KW_BY_in_tableBuckets1222 = new BitSet(new long[]{0x0000000000000000L,0x0080000000000000L});
    public static final BitSet FOLLOW_LPAREN_in_tableBuckets1224 = new BitSet(new long[]{0x0000000000000000L,0x0040000000000000L});
    public static final BitSet FOLLOW_columnNameList_in_tableBuckets1228 = new BitSet(new long[]{0x0000000000000000L,0x0100000000000000L});
    public static final BitSet FOLLOW_RPAREN_in_tableBuckets1230 = new BitSet(new long[]{0x0000000000000000L,0x0004000000000000L,0x0000000000001000L});
    public static final BitSet FOLLOW_KW_SORTED_in_tableBuckets1233 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000400L});
    public static final BitSet FOLLOW_KW_BY_in_tableBuckets1235 = new BitSet(new long[]{0x0000000000000000L,0x0080000000000000L});
    public static final BitSet FOLLOW_LPAREN_in_tableBuckets1237 = new BitSet(new long[]{0x0000000000000000L,0x0040000000000000L});
    public static final BitSet FOLLOW_columnNameOrderList_in_tableBuckets1241 = new BitSet(new long[]{0x0000000000000000L,0x0100000000000000L});
    public static final BitSet FOLLOW_RPAREN_in_tableBuckets1243 = new BitSet(new long[]{0x0000000000000000L,0x0004000000000000L});
    public static final BitSet FOLLOW_KW_INTO_in_tableBuckets1247 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000002000L});
    public static final BitSet FOLLOW_Number_in_tableBuckets1251 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000004000L});
    public static final BitSet FOLLOW_KW_BUCKETS_in_tableBuckets1253 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_KW_ROW_in_tableRowFormat1297 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000010000L});
    public static final BitSet FOLLOW_KW_FORMAT_in_tableRowFormat1299 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000020000L});
    public static final BitSet FOLLOW_KW_DELIMITED_in_tableRowFormat1301 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000000015400000L});
    public static final BitSet FOLLOW_tableRowFormatFieldIdentifier_in_tableRowFormat1303 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000000015000000L});
    public static final BitSet FOLLOW_tableRowFormatCollItemsIdentifier_in_tableRowFormat1306 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000000014000000L});
    public static final BitSet FOLLOW_tableRowFormatMapKeysIdentifier_in_tableRowFormat1309 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000000010000000L});
    public static final BitSet FOLLOW_tableRowFormatLinesIdentifier_in_tableRowFormat1312 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_KW_ROW_in_tableRowFormat1344 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000010000L});
    public static final BitSet FOLLOW_KW_FORMAT_in_tableRowFormat1346 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_KW_SERIALIZER_in_tableRowFormat1348 = new BitSet(new long[]{0x0000000000000000L,0x0001000000000000L});
    public static final BitSet FOLLOW_StringLiteral_in_tableRowFormat1352 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_tableSerializerProperties_in_tableRowFormat1354 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_KW_WITH_in_tableSerializerProperties1394 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000100000L});
    public static final BitSet FOLLOW_KW_PROPERTIES_in_tableSerializerProperties1396 = new BitSet(new long[]{0x0000000000000000L,0x0080000000000000L});
    public static final BitSet FOLLOW_LPAREN_in_tableSerializerProperties1398 = new BitSet(new long[]{0x0000000000000000L,0x0001000000000000L});
    public static final BitSet FOLLOW_propertiesList_in_tableSerializerProperties1400 = new BitSet(new long[]{0x0000000000000000L,0x0100000000000000L});
    public static final BitSet FOLLOW_RPAREN_in_tableSerializerProperties1402 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_keyValueProperty_in_propertiesList1433 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_COMMA_in_propertiesList1436 = new BitSet(new long[]{0x0000000000000000L,0x0001000000000000L});
    public static final BitSet FOLLOW_keyValueProperty_in_propertiesList1438 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_StringLiteral_in_keyValueProperty1474 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000200000L});
    public static final BitSet FOLLOW_EQUAL_in_keyValueProperty1476 = new BitSet(new long[]{0x0000000000000000L,0x0001000000000000L});
    public static final BitSet FOLLOW_StringLiteral_in_keyValueProperty1480 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_KW_FIELDS_in_tableRowFormatFieldIdentifier1515 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000800000L});
    public static final BitSet FOLLOW_KW_TERMINATED_in_tableRowFormatFieldIdentifier1517 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000400L});
    public static final BitSet FOLLOW_KW_BY_in_tableRowFormatFieldIdentifier1519 = new BitSet(new long[]{0x0000000000000000L,0x0001000000000000L});
    public static final BitSet FOLLOW_StringLiteral_in_tableRowFormatFieldIdentifier1523 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_KW_COLLECTION_in_tableRowFormatCollItemsIdentifier1560 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000002000000L});
    public static final BitSet FOLLOW_KW_ITEMS_in_tableRowFormatCollItemsIdentifier1562 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000800000L});
    public static final BitSet FOLLOW_KW_TERMINATED_in_tableRowFormatCollItemsIdentifier1564 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000400L});
    public static final BitSet FOLLOW_KW_BY_in_tableRowFormatCollItemsIdentifier1566 = new BitSet(new long[]{0x0000000000000000L,0x0001000000000000L});
    public static final BitSet FOLLOW_StringLiteral_in_tableRowFormatCollItemsIdentifier1570 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_KW_MAP_in_tableRowFormatMapKeysIdentifier1606 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000008000000L});
    public static final BitSet FOLLOW_KW_KEYS_in_tableRowFormatMapKeysIdentifier1608 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000800000L});
    public static final BitSet FOLLOW_KW_TERMINATED_in_tableRowFormatMapKeysIdentifier1610 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000400L});
    public static final BitSet FOLLOW_KW_BY_in_tableRowFormatMapKeysIdentifier1612 = new BitSet(new long[]{0x0000000000000000L,0x0001000000000000L});
    public static final BitSet FOLLOW_StringLiteral_in_tableRowFormatMapKeysIdentifier1616 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_KW_LINES_in_tableRowFormatLinesIdentifier1652 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000800000L});
    public static final BitSet FOLLOW_KW_TERMINATED_in_tableRowFormatLinesIdentifier1654 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000400L});
    public static final BitSet FOLLOW_KW_BY_in_tableRowFormatLinesIdentifier1656 = new BitSet(new long[]{0x0000000000000000L,0x0001000000000000L});
    public static final BitSet FOLLOW_StringLiteral_in_tableRowFormatLinesIdentifier1660 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_KW_STORED_in_tableFileFormat1696 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000080L});
    public static final BitSet FOLLOW_KW_AS_in_tableFileFormat1698 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000040000000L});
    public static final BitSet FOLLOW_KW_SEQUENCEFILE_in_tableFileFormat1700 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_KW_LOCATION_in_tableLocation1728 = new BitSet(new long[]{0x0000000000000000L,0x0001000000000000L});
    public static final BitSet FOLLOW_StringLiteral_in_tableLocation1732 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_columnNameType_in_columnNameTypeList1760 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_COMMA_in_columnNameTypeList1763 = new BitSet(new long[]{0x0000000000000000L,0x0040000000000000L});
    public static final BitSet FOLLOW_columnNameType_in_columnNameTypeList1765 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_columnName_in_columnNameList1793 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_COMMA_in_columnNameList1796 = new BitSet(new long[]{0x0000000000000000L,0x0040000000000000L});
    public static final BitSet FOLLOW_columnName_in_columnNameList1798 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_Identifier_in_columnName1832 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_columnNameOrder_in_columnNameOrderList1849 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_COMMA_in_columnNameOrderList1852 = new BitSet(new long[]{0x0000000000000000L,0x0040000000000000L});
    public static final BitSet FOLLOW_columnNameOrder_in_columnNameOrderList1854 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_Identifier_in_columnNameOrder1882 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000000300000000L});
    public static final BitSet FOLLOW_KW_ASC_in_columnNameOrder1887 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_KW_DESC_in_columnNameOrder1893 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Identifier_in_columnNameType1958 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x00001FFC04000000L});
    public static final BitSet FOLLOW_colType_in_columnNameType1960 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000000000000100L});
    public static final BitSet FOLLOW_KW_COMMENT_in_columnNameType1963 = new BitSet(new long[]{0x0000000000000000L,0x0001000000000000L});
    public static final BitSet FOLLOW_StringLiteral_in_columnNameType1967 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_primitiveType_in_colType2045 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_listType_in_colType2053 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_mapType_in_colType2061 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_KW_TINYINT_in_primitiveType2078 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_KW_INT_in_primitiveType2099 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_KW_BIGINT_in_primitiveType2124 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_KW_BOOLEAN_in_primitiveType2146 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_KW_FLOAT_in_primitiveType2167 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_KW_DOUBLE_in_primitiveType2190 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_KW_DATE_in_primitiveType2212 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_KW_DATETIME_in_primitiveType2236 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_KW_TIMESTAMP_in_primitiveType2256 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_KW_STRING_in_primitiveType2275 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_KW_ARRAY_in_listType2306 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000200000000000L});
    public static final BitSet FOLLOW_LESSTHAN_in_listType2308 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x00000FFC00000000L});
    public static final BitSet FOLLOW_primitiveType_in_listType2310 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000400000000000L});
    public static final BitSet FOLLOW_GREATERTHAN_in_listType2312 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_KW_MAP_in_mapType2339 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000200000000000L});
    public static final BitSet FOLLOW_LESSTHAN_in_mapType2341 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x00000FFC00000000L});
    public static final BitSet FOLLOW_primitiveType_in_mapType2345 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_COMMA_in_mapType2347 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x00000FFC00000000L});
    public static final BitSet FOLLOW_primitiveType_in_mapType2351 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000400000000000L});
    public static final BitSet FOLLOW_GREATERTHAN_in_mapType2353 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_KW_UNION_in_queryOperator2386 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0001000000000000L});
    public static final BitSet FOLLOW_KW_ALL_in_queryOperator2388 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_queryStatement_in_queryStatementExpression2412 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000800000000000L});
    public static final BitSet FOLLOW_queryOperator_in_queryStatementExpression2415 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0412000000000000L});
    public static final BitSet FOLLOW_queryStatementExpression_in_queryStatementExpression2418 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000800000000000L});
    public static final BitSet FOLLOW_fromClause_in_queryStatement2441 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0012000000000000L});
    public static final BitSet FOLLOW_body_in_queryStatement2451 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0012000000000000L});
    public static final BitSet FOLLOW_regular_body_in_queryStatement2473 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_insertClause_in_regular_body2492 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0010000000000000L});
    public static final BitSet FOLLOW_selectClause_in_regular_body2497 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0400000000000000L});
    public static final BitSet FOLLOW_fromClause_in_regular_body2502 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0008000000000000L,0x00000000000001E0L});
    public static final BitSet FOLLOW_whereClause_in_regular_body2507 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0008000000000000L,0x00000000000001C0L});
    public static final BitSet FOLLOW_groupByClause_in_regular_body2513 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0008000000000000L,0x0000000000000180L});
    public static final BitSet FOLLOW_orderByClause_in_regular_body2519 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0008000000000000L,0x0000000000000100L});
    public static final BitSet FOLLOW_clusterByClause_in_regular_body2525 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0008000000000000L});
    public static final BitSet FOLLOW_limitClause_in_regular_body2532 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_selectClause_in_regular_body2574 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0400000000000000L});
    public static final BitSet FOLLOW_fromClause_in_regular_body2579 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0008000000000000L,0x00000000000001E0L});
    public static final BitSet FOLLOW_whereClause_in_regular_body2584 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0008000000000000L,0x00000000000001C0L});
    public static final BitSet FOLLOW_groupByClause_in_regular_body2590 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0008000000000000L,0x0000000000000180L});
    public static final BitSet FOLLOW_orderByClause_in_regular_body2596 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0008000000000000L,0x0000000000000100L});
    public static final BitSet FOLLOW_clusterByClause_in_regular_body2602 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0008000000000000L});
    public static final BitSet FOLLOW_limitClause_in_regular_body2609 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_insertClause_in_body2668 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0010000000000000L});
    public static final BitSet FOLLOW_selectClause_in_body2673 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0008000000000000L,0x00000000000001E0L});
    public static final BitSet FOLLOW_whereClause_in_body2678 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0008000000000000L,0x00000000000001C0L});
    public static final BitSet FOLLOW_groupByClause_in_body2684 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0008000000000000L,0x0000000000000180L});
    public static final BitSet FOLLOW_orderByClause_in_body2690 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0008000000000000L,0x0000000000000100L});
    public static final BitSet FOLLOW_clusterByClause_in_body2697 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0008000000000000L});
    public static final BitSet FOLLOW_limitClause_in_body2704 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_selectClause_in_body2741 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0008000000000000L,0x00000000000001E0L});
    public static final BitSet FOLLOW_whereClause_in_body2746 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0008000000000000L,0x00000000000001C0L});
    public static final BitSet FOLLOW_groupByClause_in_body2752 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0008000000000000L,0x0000000000000180L});
    public static final BitSet FOLLOW_orderByClause_in_body2758 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0008000000000000L,0x0000000000000100L});
    public static final BitSet FOLLOW_clusterByClause_in_body2765 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0008000000000000L});
    public static final BitSet FOLLOW_limitClause_in_body2772 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_KW_INSERT_in_insertClause2828 = new BitSet(new long[]{0x0000000000000000L,0x0002000000000000L});
    public static final BitSet FOLLOW_KW_OVERWRITE_in_insertClause2830 = new BitSet(new long[]{0x0000000000000000L,0x0008400000000000L,0x0004000000000000L});
    public static final BitSet FOLLOW_destination_in_insertClause2832 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_KW_LOCAL_in_destination2860 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0004000000000000L});
    public static final BitSet FOLLOW_KW_DIRECTORY_in_destination2862 = new BitSet(new long[]{0x0000000000000000L,0x0001000000000000L});
    public static final BitSet FOLLOW_StringLiteral_in_destination2864 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_KW_DIRECTORY_in_destination2879 = new BitSet(new long[]{0x0000000000000000L,0x0001000000000000L});
    public static final BitSet FOLLOW_StringLiteral_in_destination2881 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_KW_TABLE_in_destination2896 = new BitSet(new long[]{0x0000000000000000L,0x0040000000000000L});
    public static final BitSet FOLLOW_tabName_in_destination2898 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_KW_LIMIT_in_limitClause2922 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000002000L});
    public static final BitSet FOLLOW_Number_in_limitClause2926 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_KW_SELECT_in_selectClause2957 = new BitSet(new long[]{0x0000000000000000L,0x00C1000000000000L,0x0161000000002000L,0x00000006000B9600L});
    public static final BitSet FOLLOW_KW_ALL_in_selectClause2960 = new BitSet(new long[]{0x0000000000000000L,0x00C1000000000000L,0x0140000000002000L,0x00000006000B9600L});
    public static final BitSet FOLLOW_KW_DISTINCT_in_selectClause2966 = new BitSet(new long[]{0x0000000000000000L,0x00C1000000000000L,0x0140000000002000L,0x00000006000B9600L});
    public static final BitSet FOLLOW_selectList_in_selectClause2974 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_selectItem_in_selectList3045 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_COMMA_in_selectList3053 = new BitSet(new long[]{0x0000000000000000L,0x00C1000000000000L,0x0140000000002000L,0x00000006000B9600L});
    public static final BitSet FOLLOW_selectItem_in_selectList3056 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_trfmClause_in_selectItem3087 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_selectExpression_in_selectItem3104 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000000000000080L});
    public static final BitSet FOLLOW_KW_AS_in_selectItem3108 = new BitSet(new long[]{0x0000000000000000L,0x0040000000000000L});
    public static final BitSet FOLLOW_Identifier_in_selectItem3110 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_KW_TRANSFORM_in_trfmClause3149 = new BitSet(new long[]{0x0000000000000000L,0x0080000000000000L});
    public static final BitSet FOLLOW_LPAREN_in_trfmClause3155 = new BitSet(new long[]{0x0000000000000000L,0x0040000000000000L,0x0100000000000000L});
    public static final BitSet FOLLOW_columnList_in_trfmClause3157 = new BitSet(new long[]{0x0000000000000000L,0x0100000000000000L});
    public static final BitSet FOLLOW_RPAREN_in_trfmClause3159 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000080L});
    public static final BitSet FOLLOW_KW_AS_in_trfmClause3165 = new BitSet(new long[]{0x0000000000000000L,0x0080000000000000L});
    public static final BitSet FOLLOW_LPAREN_in_trfmClause3172 = new BitSet(new long[]{0x0000000000000000L,0x0040000000000000L});
    public static final BitSet FOLLOW_aliasList_in_trfmClause3174 = new BitSet(new long[]{0x0000000000000000L,0x0100000000000000L});
    public static final BitSet FOLLOW_RPAREN_in_trfmClause3176 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0080000000000000L});
    public static final BitSet FOLLOW_KW_USING_in_trfmClause3182 = new BitSet(new long[]{0x0000000000000000L,0x0001000000000000L});
    public static final BitSet FOLLOW_StringLiteral_in_trfmClause3184 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expression_in_selectExpression3221 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_tableAllColumns_in_selectExpression3225 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Identifier_in_tableAllColumns3248 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0100000000000000L});
    public static final BitSet FOLLOW_DOT_in_tableAllColumns3250 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0200000000000000L});
    public static final BitSet FOLLOW_STAR_in_tableAllColumns3252 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Identifier_in_tableColumn3289 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0100000000000000L});
    public static final BitSet FOLLOW_DOT_in_tableColumn3293 = new BitSet(new long[]{0x0000000000000000L,0x0040000000000000L});
    public static final BitSet FOLLOW_Identifier_in_tableColumn3297 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_tableColumn_in_columnList3331 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_COMMA_in_columnList3334 = new BitSet(new long[]{0x0000000000000000L,0x0040000000000000L,0x0100000000000000L});
    public static final BitSet FOLLOW_tableColumn_in_columnList3336 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_Identifier_in_aliasList3368 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_COMMA_in_aliasList3371 = new BitSet(new long[]{0x0000000000000000L,0x0040000000000000L});
    public static final BitSet FOLLOW_Identifier_in_aliasList3373 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_KW_FROM_in_fromClause3412 = new BitSet(new long[]{0x0000000000000000L,0x00C0000000000000L});
    public static final BitSet FOLLOW_joinSource_in_fromClause3414 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_KW_FROM_in_fromClause3430 = new BitSet(new long[]{0x0000000000000000L,0x00C0000000000000L});
    public static final BitSet FOLLOW_fromSource_in_fromClause3432 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_fromSource_in_joinSource3465 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0xB000000000000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_joinToken_in_joinSource3474 = new BitSet(new long[]{0x0000000000000000L,0x00C0000000000000L});
    public static final BitSet FOLLOW_fromSource_in_joinSource3477 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0xB800000000000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_KW_ON_in_joinSource3480 = new BitSet(new long[]{0x0000000000000000L,0x00C1000000000000L,0x0100000000002000L,0x0000000600039600L});
    public static final BitSet FOLLOW_precedenceEqualExpression_in_joinSource3483 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0xB000000000000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_KW_JOIN_in_joinToken3511 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_KW_LEFT_in_joinToken3543 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x4000000000000000L});
    public static final BitSet FOLLOW_KW_OUTER_in_joinToken3545 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x1000000000000000L});
    public static final BitSet FOLLOW_KW_JOIN_in_joinToken3547 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_KW_RIGHT_in_joinToken3562 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x4000000000000000L});
    public static final BitSet FOLLOW_KW_OUTER_in_joinToken3564 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x1000000000000000L});
    public static final BitSet FOLLOW_KW_JOIN_in_joinToken3566 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_KW_FULL_in_joinToken3580 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x4000000000000000L});
    public static final BitSet FOLLOW_KW_OUTER_in_joinToken3582 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x1000000000000000L});
    public static final BitSet FOLLOW_KW_JOIN_in_joinToken3584 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_tableSource_in_fromSource3613 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_subQuerySource_in_fromSource3617 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_KW_TABLESAMPLE_in_tableSample3643 = new BitSet(new long[]{0x0000000000000000L,0x0080000000000000L});
    public static final BitSet FOLLOW_LPAREN_in_tableSample3645 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_KW_BUCKET_in_tableSample3647 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000002000L});
    public static final BitSet FOLLOW_Number_in_tableSample3652 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_KW_OUT_in_tableSample3655 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000010L});
    public static final BitSet FOLLOW_KW_OF_in_tableSample3657 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000002000L});
    public static final BitSet FOLLOW_Number_in_tableSample3662 = new BitSet(new long[]{0x0000000000000000L,0x0100000000000000L,0x0800000000000000L});
    public static final BitSet FOLLOW_KW_ON_in_tableSample3666 = new BitSet(new long[]{0x0000000000000000L,0x0040000000000000L});
    public static final BitSet FOLLOW_Identifier_in_tableSample3670 = new BitSet(new long[]{0x0000000000000000L,0x0100000000000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_COMMA_in_tableSample3673 = new BitSet(new long[]{0x0000000000000000L,0x0040000000000000L});
    public static final BitSet FOLLOW_Identifier_in_tableSample3677 = new BitSet(new long[]{0x0000000000000000L,0x0100000000000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_RPAREN_in_tableSample3683 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Identifier_in_tableSource3722 = new BitSet(new long[]{0x0000000000000002L,0x0040000000000000L,0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_tableSample_in_tableSource3727 = new BitSet(new long[]{0x0000000000000002L,0x0040000000000000L});
    public static final BitSet FOLLOW_Identifier_in_tableSource3734 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LPAREN_in_subQuerySource3776 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0412000000000000L});
    public static final BitSet FOLLOW_queryStatementExpression_in_subQuerySource3778 = new BitSet(new long[]{0x0000000000000000L,0x0100000000000000L});
    public static final BitSet FOLLOW_RPAREN_in_subQuerySource3780 = new BitSet(new long[]{0x0000000000000000L,0x0040000000000000L});
    public static final BitSet FOLLOW_Identifier_in_subQuerySource3782 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_KW_WHERE_in_whereClause3823 = new BitSet(new long[]{0x0000000000000000L,0x00C1000000000000L,0x0100000000002000L,0x00000006000B9600L});
    public static final BitSet FOLLOW_searchCondition_in_whereClause3825 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expression_in_searchCondition3854 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_KW_GROUP_in_groupByClause3878 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000400L});
    public static final BitSet FOLLOW_KW_BY_in_groupByClause3880 = new BitSet(new long[]{0x0000000000000000L,0x00C1000000000000L,0x0100000000002000L,0x00000006000B9600L});
    public static final BitSet FOLLOW_groupByExpression_in_groupByClause3886 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_COMMA_in_groupByClause3894 = new BitSet(new long[]{0x0000000000000000L,0x00C1000000000000L,0x0100000000002000L,0x00000006000B9600L});
    public static final BitSet FOLLOW_groupByExpression_in_groupByClause3896 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_expression_in_groupByExpression3933 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_KW_ORDER_in_orderByClause3955 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000400L});
    public static final BitSet FOLLOW_KW_BY_in_orderByClause3957 = new BitSet(new long[]{0x0000000000000000L,0x00C1000000000000L,0x0100000000002000L,0x00000006000B9600L});
    public static final BitSet FOLLOW_orderByExpression_in_orderByClause3963 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_COMMA_in_orderByClause3971 = new BitSet(new long[]{0x0000000000000000L,0x00C1000000000000L,0x0100000000002000L,0x00000006000B9600L});
    public static final BitSet FOLLOW_orderByExpression_in_orderByClause3973 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_expression_in_orderByExpression4010 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000000300000000L});
    public static final BitSet FOLLOW_set_in_orderByExpression4016 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_KW_CLUSTER_in_clusterByClause4044 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000400L});
    public static final BitSet FOLLOW_KW_BY_in_clusterByClause4046 = new BitSet(new long[]{0x0000000000000000L,0x0040000000000000L});
    public static final BitSet FOLLOW_Identifier_in_clusterByClause4053 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_COMMA_in_clusterByClause4062 = new BitSet(new long[]{0x0000000000000000L,0x0040000000000000L});
    public static final BitSet FOLLOW_Identifier_in_clusterByClause4064 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_expression_in_clusterByExpression4097 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Identifier_in_function4121 = new BitSet(new long[]{0x0000000000000000L,0x0080000000000000L});
    public static final BitSet FOLLOW_LPAREN_in_function4127 = new BitSet(new long[]{0x0000000000000000L,0x01C1000000000000L,0x0120000000002000L,0x00000006000B9600L});
    public static final BitSet FOLLOW_KW_DISTINCT_in_function4144 = new BitSet(new long[]{0x0000000000000000L,0x00C1000000000000L,0x0100000000002000L,0x00000006000B9600L});
    public static final BitSet FOLLOW_expression_in_function4158 = new BitSet(new long[]{0x0000000000000000L,0x0100000000000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_COMMA_in_function4171 = new BitSet(new long[]{0x0000000000000000L,0x00C1000000000000L,0x0100000000002000L,0x00000006000B9600L});
    public static final BitSet FOLLOW_expression_in_function4173 = new BitSet(new long[]{0x0000000000000000L,0x0100000000000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_RPAREN_in_function4192 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_KW_CAST_in_castExpression4264 = new BitSet(new long[]{0x0000000000000000L,0x0080000000000000L});
    public static final BitSet FOLLOW_LPAREN_in_castExpression4270 = new BitSet(new long[]{0x0000000000000000L,0x00C1000000000000L,0x0100000000002000L,0x00000006000B9600L});
    public static final BitSet FOLLOW_expression_in_castExpression4283 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000080L});
    public static final BitSet FOLLOW_KW_AS_in_castExpression4295 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x00000FFC00000000L});
    public static final BitSet FOLLOW_primitiveType_in_castExpression4307 = new BitSet(new long[]{0x0000000000000000L,0x0100000000000000L});
    public static final BitSet FOLLOW_RPAREN_in_castExpression4313 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Number_in_constant4348 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_StringLiteral_in_constant4356 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_charSetStringLiteral_in_constant4364 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_booleanValue_in_constant4372 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CharSetName_in_charSetStringLiteral4396 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000800L});
    public static final BitSet FOLLOW_CharSetLiteral_in_charSetStringLiteral4400 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_precedenceOrExpression_in_expression4428 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_KW_NULL_in_atomExpression4444 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_constant_in_atomExpression4456 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_function_in_atomExpression4464 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_castExpression_in_atomExpression4472 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_tableColumn_in_atomExpression4480 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LPAREN_in_atomExpression4488 = new BitSet(new long[]{0x0000000000000000L,0x00C1000000000000L,0x0100000000002000L,0x00000006000B9600L});
    public static final BitSet FOLLOW_expression_in_atomExpression4491 = new BitSet(new long[]{0x0000000000000000L,0x0100000000000000L});
    public static final BitSet FOLLOW_RPAREN_in_atomExpression4493 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_atomExpression_in_precedenceFieldExpression4516 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0100000000000000L,0x0000000000002000L});
    public static final BitSet FOLLOW_LSQUARE_in_precedenceFieldExpression4520 = new BitSet(new long[]{0x0000000000000000L,0x00C1000000000000L,0x0100000000002000L,0x00000006000B9600L});
    public static final BitSet FOLLOW_expression_in_precedenceFieldExpression4523 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000004000L});
    public static final BitSet FOLLOW_RSQUARE_in_precedenceFieldExpression4525 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOT_in_precedenceFieldExpression4532 = new BitSet(new long[]{0x0000000000000000L,0x0040000000000000L});
    public static final BitSet FOLLOW_Identifier_in_precedenceFieldExpression4535 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0100000000000000L,0x0000000000002000L});
    public static final BitSet FOLLOW_set_in_precedenceUnaryOperator0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_precedenceFieldExpression_in_precedenceUnaryExpression4590 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_KW_IS_in_precedenceUnaryExpression4592 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000001000L});
    public static final BitSet FOLLOW_KW_NULL_in_precedenceUnaryExpression4594 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_precedenceFieldExpression_in_precedenceUnaryExpression4612 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_KW_IS_in_precedenceUnaryExpression4614 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_KW_NOT_in_precedenceUnaryExpression4616 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000001000L});
    public static final BitSet FOLLOW_KW_NULL_in_precedenceUnaryExpression4618 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_precedenceUnaryOperator_in_precedenceUnaryExpression4637 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_precedenceFieldExpression_in_precedenceUnaryExpression4642 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BITWISEXOR_in_precedenceBitwiseXorOperator4664 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_precedenceUnaryExpression_in_precedenceBitwiseXorExpression4685 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000000000000000L,0x0000000000100000L});
    public static final BitSet FOLLOW_precedenceBitwiseXorOperator_in_precedenceBitwiseXorExpression4688 = new BitSet(new long[]{0x0000000000000000L,0x00C1000000000000L,0x0100000000002000L,0x0000000600039600L});
    public static final BitSet FOLLOW_precedenceUnaryExpression_in_precedenceBitwiseXorExpression4691 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000000000000000L,0x0000000000100000L});
    public static final BitSet FOLLOW_set_in_precedenceStarOperator0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_precedenceBitwiseXorExpression_in_precedenceStarExpression4744 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0200000000000000L,0x0000000000600000L});
    public static final BitSet FOLLOW_precedenceStarOperator_in_precedenceStarExpression4747 = new BitSet(new long[]{0x0000000000000000L,0x00C1000000000000L,0x0100000000002000L,0x0000000600039600L});
    public static final BitSet FOLLOW_precedenceBitwiseXorExpression_in_precedenceStarExpression4750 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0200000000000000L,0x0000000000600000L});
    public static final BitSet FOLLOW_set_in_precedencePlusOperator0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_precedenceStarExpression_in_precedencePlusExpression4799 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000000000000000L,0x0000000000018000L});
    public static final BitSet FOLLOW_precedencePlusOperator_in_precedencePlusExpression4802 = new BitSet(new long[]{0x0000000000000000L,0x00C1000000000000L,0x0100000000002000L,0x0000000600039600L});
    public static final BitSet FOLLOW_precedenceStarExpression_in_precedencePlusExpression4805 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000000000000000L,0x0000000000018000L});
    public static final BitSet FOLLOW_AMPERSAND_in_precedenceAmpersandOperator4829 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_precedencePlusExpression_in_precedenceAmpersandExpression4850 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000000000000000L,0x0000000000800000L});
    public static final BitSet FOLLOW_precedenceAmpersandOperator_in_precedenceAmpersandExpression4853 = new BitSet(new long[]{0x0000000000000000L,0x00C1000000000000L,0x0100000000002000L,0x0000000600039600L});
    public static final BitSet FOLLOW_precedencePlusExpression_in_precedenceAmpersandExpression4856 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000000000000000L,0x0000000000800000L});
    public static final BitSet FOLLOW_BITWISEOR_in_precedenceBitwiseOrOperator4880 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_precedenceAmpersandExpression_in_precedenceBitwiseOrExpression4901 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000000000000000L,0x0000000001000000L});
    public static final BitSet FOLLOW_precedenceBitwiseOrOperator_in_precedenceBitwiseOrExpression4904 = new BitSet(new long[]{0x0000000000000000L,0x00C1000000000000L,0x0100000000002000L,0x0000000600039600L});
    public static final BitSet FOLLOW_precedenceAmpersandExpression_in_precedenceBitwiseOrExpression4907 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000000000000000L,0x0000000001000000L});
    public static final BitSet FOLLOW_set_in_precedenceEqualOperator0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_precedenceBitwiseOrExpression_in_precedenceEqualExpression4988 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000600000200000L,0x000000007E000000L});
    public static final BitSet FOLLOW_precedenceEqualOperator_in_precedenceEqualExpression4991 = new BitSet(new long[]{0x0000000000000000L,0x00C1000000000000L,0x0100000000002000L,0x0000000600039600L});
    public static final BitSet FOLLOW_precedenceBitwiseOrExpression_in_precedenceEqualExpression4994 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000600000200000L,0x000000007E000000L});
    public static final BitSet FOLLOW_KW_NOT_in_precedenceNotOperator5018 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_precedenceNotOperator_in_precedenceNotExpression5040 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_precedenceEqualExpression_in_precedenceNotExpression5045 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_KW_AND_in_precedenceAndOperator5067 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_precedenceNotExpression_in_precedenceAndExpression5088 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000000000000000L,0x0000000080000000L});
    public static final BitSet FOLLOW_precedenceAndOperator_in_precedenceAndExpression5091 = new BitSet(new long[]{0x0000000000000000L,0x00C1000000000000L,0x0100000000002000L,0x00000006000B9600L});
    public static final BitSet FOLLOW_precedenceNotExpression_in_precedenceAndExpression5094 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000000000000000L,0x0000000080000000L});
    public static final BitSet FOLLOW_KW_OR_in_precedenceOrOperator5118 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_precedenceAndExpression_in_precedenceOrExpression5139 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000000000000000L,0x0000000100000000L});
    public static final BitSet FOLLOW_precedenceOrOperator_in_precedenceOrExpression5142 = new BitSet(new long[]{0x0000000000000000L,0x00C1000000000000L,0x0100000000002000L,0x00000006000B9600L});
    public static final BitSet FOLLOW_precedenceAndExpression_in_precedenceOrExpression5145 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000000000000000L,0x0000000100000000L});
    public static final BitSet FOLLOW_KW_TRUE_in_booleanValue5169 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_KW_FALSE_in_booleanValue5174 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Identifier_in_tabName5194 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000000000000000L,0x0000000800000000L});
    public static final BitSet FOLLOW_partitionSpec_in_tabName5196 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_KW_PARTITION_in_partitionSpec5236 = new BitSet(new long[]{0x0000000000000000L,0x0080000000000000L});
    public static final BitSet FOLLOW_LPAREN_in_partitionSpec5243 = new BitSet(new long[]{0x0000000000000000L,0x0040000000000000L});
    public static final BitSet FOLLOW_partitionVal_in_partitionSpec5245 = new BitSet(new long[]{0x0000000000000000L,0x0100000000000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_COMMA_in_partitionSpec5248 = new BitSet(new long[]{0x0000000000000000L,0x0040000000000000L});
    public static final BitSet FOLLOW_partitionVal_in_partitionSpec5251 = new BitSet(new long[]{0x0000000000000000L,0x0100000000000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_RPAREN_in_partitionSpec5256 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Identifier_in_partitionVal5287 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000200000L});
    public static final BitSet FOLLOW_EQUAL_in_partitionVal5289 = new BitSet(new long[]{0x0000000000000000L,0x0001000000000000L,0x0000000000002000L,0x0000000600000400L});
    public static final BitSet FOLLOW_constant_in_partitionVal5291 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_queryOperator_in_synpred522415 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0412000000000000L});
    public static final BitSet FOLLOW_queryStatementExpression_in_synpred522418 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_KW_FROM_in_synpred883412 = new BitSet(new long[]{0x0000000000000000L,0x00C0000000000000L});
    public static final BitSet FOLLOW_joinSource_in_synpred883414 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_precedenceFieldExpression_in_synpred1194590 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_KW_IS_in_synpred1194592 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000001000L});
    public static final BitSet FOLLOW_KW_NULL_in_synpred1194594 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_precedenceFieldExpression_in_synpred1204612 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_KW_IS_in_synpred1204614 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_KW_NOT_in_synpred1204616 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000001000L});
    public static final BitSet FOLLOW_KW_NULL_in_synpred1204618 = new BitSet(new long[]{0x0000000000000002L});

}