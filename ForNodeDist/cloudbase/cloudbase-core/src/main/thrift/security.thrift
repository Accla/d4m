namespace java cloudbase.core.security.thrift

exception ThriftSecurityException {
    1:string user,
    2:bool baduserpass
}

struct AuthInfo {
    1:string user,
    2:binary password
}
