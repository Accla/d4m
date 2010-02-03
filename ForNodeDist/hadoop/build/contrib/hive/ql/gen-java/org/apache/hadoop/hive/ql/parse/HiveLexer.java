// $ANTLR 3.0.1 /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g 2009-01-07 13:41:42
package org.apache.hadoop.hive.ql.parse;

import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

public class HiveLexer extends Lexer {
    public static final int KW_ALTER=122;
    public static final int TOK_FUNCTIONDI=21;
    public static final int KW_PARTITIONED=137;
    public static final int KW_LIKE=220;
    public static final int TOK_TABREF=15;
    public static final int STAR=185;
    public static final int KW_JOIN=188;
    public static final int LSQUARE=205;
    public static final int KW_ITEMS=153;
    public static final int MOD=214;
    public static final int KW_FORMAT=144;
    public static final int KW_ROW=143;
    public static final int TOK_OP_EQ=23;
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
    public static final int KW_SELECT=180;
    public static final int TOK_SELEXPR=8;
    public static final int WS=234;
    public static final int TOK_TABLEROWFORMATFIELD=87;
    public static final int KW_REPLACE=126;
    public static final int TOK_MAP=70;
    public static final int KW_BUCKET=194;
    public static final int KW_LOAD=108;
    public static final int KW_GROUP=198;
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
    public static final int KW_BOOLEAN=165;
    public static final int CharSetName=202;
    public static final int TOK_DOUBLE=64;
    public static final int KW_REGEXP=222;
    public static final int KW_DIRECTORY=178;
    public static final int TOK_DATETIME=66;
    public static final int TOK_DESCTABLE=72;
    public static final int HexDigit=231;
    public static final int LPAREN=119;
    public static final int KW_PARTITION=227;
    public static final int KW_FROM=186;
    public static final int GREATERTHANOREQUALTO=219;
    public static final int TOK_TIMESTAMP=67;
    public static final int KW_TERMINATED=151;
    public static final int TOK_TRUE=42;
    public static final int TOK_CREATEEXTTABLE=79;
    public static final int TOK_LOCAL_DIR=14;
    public static final int PLUS=207;
    public static final int KW_OUTER=190;
    public static final int KW_IS=210;
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
    public static final int Tokens=236;
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
    public static final int KW_CREATE=116;
    public static final int Exponent=233;
    public static final int KW_TRUE=225;
    public static final int KW_LIMIT=179;
    public static final int KW_WITH=147;
    public static final int KW_BIGINT=164;
    public static final int TOK_INT=60;
    public static final int TOKTABLESERDEPROPERTY=105;
    public static final int KW_RIGHT=191;
    public static final int TOK_ORDERBY=48;
    public static final int Number=141;
    public static final int KW_EXTERNAL=117;
    public static final int COMMA=128;
    public static final int EQUAL=149;
    public static final int TOK_JOIN=51;
    public static final int TILDE=209;
    public static final int TOK_DESTINATION=17;
    public static final int DOT=184;
    public static final int TOK_OP_BITAND=34;
    public static final int TOK_TAB=10;
    public static final int KW_UNION=175;
    public static final int KW_TEMPORARY=133;
    public static final int KW_CAST=201;
    public static final int KW_FALSE=226;
    public static final int TOK_EXPLAIN=100;
    public static final int TOK_OP_BITOR=36;
    public static final int RSQUARE=206;
    public static final int KW_STORED=157;
    public static final int TOK_PARTSPEC=11;
    public static final int Digit=232;
    public static final int TOK_BOOLEAN=62;
    public static final int KW_DATA=109;
    public static final int TOK_LIMIT=104;
    public static final int TOK_TABSORTCOLNAMEASC=96;
    public static final int KW_SHOW=130;
    public static final int TOK_OP_BITNOT=35;
    public static final int KW_DATE=168;
    public static final int TOK_TABCOLLIST=81;
    public static final int KW_INTO=114;
    public static final int KW_OR=224;
    public static final int TOK_TABLEROWFORMATCOLLITEMS=88;
    public static final int KW_ON=187;
    public static final int KW_AS=135;
    public static final int KW_OF=196;
    public static final int TOK_OP_SUB=31;
    public static final int TOK_TABLELOCATION=93;
    public static final int KW_PROPERTIES=148;
    public HiveLexer() {;} 
    public HiveLexer(CharStream input) {
        super(input);
    }
    public String getGrammarFileName() { return "/root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g"; }

    // $ANTLR start KW_TRUE
    public final void mKW_TRUE() throws RecognitionException {
        try {
            int _type = KW_TRUE;
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:759:9: ( 'TRUE' )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:759:11: 'TRUE'
            {
            match("TRUE"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end KW_TRUE

    // $ANTLR start KW_FALSE
    public final void mKW_FALSE() throws RecognitionException {
        try {
            int _type = KW_FALSE;
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:760:10: ( 'FALSE' )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:760:12: 'FALSE'
            {
            match("FALSE"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end KW_FALSE

    // $ANTLR start KW_ALL
    public final void mKW_ALL() throws RecognitionException {
        try {
            int _type = KW_ALL;
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:761:8: ( 'ALL' )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:761:10: 'ALL'
            {
            match("ALL"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end KW_ALL

    // $ANTLR start KW_AND
    public final void mKW_AND() throws RecognitionException {
        try {
            int _type = KW_AND;
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:762:8: ( 'AND' )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:762:10: 'AND'
            {
            match("AND"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end KW_AND

    // $ANTLR start KW_OR
    public final void mKW_OR() throws RecognitionException {
        try {
            int _type = KW_OR;
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:763:7: ( 'OR' )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:763:9: 'OR'
            {
            match("OR"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end KW_OR

    // $ANTLR start KW_NOT
    public final void mKW_NOT() throws RecognitionException {
        try {
            int _type = KW_NOT;
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:764:8: ( 'NOT' )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:764:10: 'NOT'
            {
            match("NOT"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end KW_NOT

    // $ANTLR start KW_LIKE
    public final void mKW_LIKE() throws RecognitionException {
        try {
            int _type = KW_LIKE;
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:765:9: ( 'LIKE' )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:765:11: 'LIKE'
            {
            match("LIKE"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end KW_LIKE

    // $ANTLR start KW_ASC
    public final void mKW_ASC() throws RecognitionException {
        try {
            int _type = KW_ASC;
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:767:8: ( 'ASC' )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:767:10: 'ASC'
            {
            match("ASC"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end KW_ASC

    // $ANTLR start KW_DESC
    public final void mKW_DESC() throws RecognitionException {
        try {
            int _type = KW_DESC;
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:768:9: ( 'DESC' )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:768:11: 'DESC'
            {
            match("DESC"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end KW_DESC

    // $ANTLR start KW_ORDER
    public final void mKW_ORDER() throws RecognitionException {
        try {
            int _type = KW_ORDER;
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:769:10: ( 'ORDER' )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:769:12: 'ORDER'
            {
            match("ORDER"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end KW_ORDER

    // $ANTLR start KW_BY
    public final void mKW_BY() throws RecognitionException {
        try {
            int _type = KW_BY;
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:770:7: ( 'BY' )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:770:9: 'BY'
            {
            match("BY"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end KW_BY

    // $ANTLR start KW_GROUP
    public final void mKW_GROUP() throws RecognitionException {
        try {
            int _type = KW_GROUP;
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:771:10: ( 'GROUP' )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:771:12: 'GROUP'
            {
            match("GROUP"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end KW_GROUP

    // $ANTLR start KW_WHERE
    public final void mKW_WHERE() throws RecognitionException {
        try {
            int _type = KW_WHERE;
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:772:10: ( 'WHERE' )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:772:12: 'WHERE'
            {
            match("WHERE"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end KW_WHERE

    // $ANTLR start KW_FROM
    public final void mKW_FROM() throws RecognitionException {
        try {
            int _type = KW_FROM;
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:773:9: ( 'FROM' )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:773:11: 'FROM'
            {
            match("FROM"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end KW_FROM

    // $ANTLR start KW_AS
    public final void mKW_AS() throws RecognitionException {
        try {
            int _type = KW_AS;
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:774:7: ( 'AS' )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:774:9: 'AS'
            {
            match("AS"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end KW_AS

    // $ANTLR start KW_SELECT
    public final void mKW_SELECT() throws RecognitionException {
        try {
            int _type = KW_SELECT;
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:775:11: ( 'SELECT' )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:775:13: 'SELECT'
            {
            match("SELECT"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end KW_SELECT

    // $ANTLR start KW_DISTINCT
    public final void mKW_DISTINCT() throws RecognitionException {
        try {
            int _type = KW_DISTINCT;
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:776:13: ( 'DISTINCT' )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:776:15: 'DISTINCT'
            {
            match("DISTINCT"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end KW_DISTINCT

    // $ANTLR start KW_INSERT
    public final void mKW_INSERT() throws RecognitionException {
        try {
            int _type = KW_INSERT;
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:777:11: ( 'INSERT' )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:777:13: 'INSERT'
            {
            match("INSERT"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end KW_INSERT

    // $ANTLR start KW_OVERWRITE
    public final void mKW_OVERWRITE() throws RecognitionException {
        try {
            int _type = KW_OVERWRITE;
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:778:14: ( 'OVERWRITE' )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:778:16: 'OVERWRITE'
            {
            match("OVERWRITE"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end KW_OVERWRITE

    // $ANTLR start KW_OUTER
    public final void mKW_OUTER() throws RecognitionException {
        try {
            int _type = KW_OUTER;
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:779:10: ( 'OUTER' )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:779:12: 'OUTER'
            {
            match("OUTER"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end KW_OUTER

    // $ANTLR start KW_JOIN
    public final void mKW_JOIN() throws RecognitionException {
        try {
            int _type = KW_JOIN;
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:780:9: ( 'JOIN' )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:780:11: 'JOIN'
            {
            match("JOIN"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end KW_JOIN

    // $ANTLR start KW_LEFT
    public final void mKW_LEFT() throws RecognitionException {
        try {
            int _type = KW_LEFT;
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:781:9: ( 'LEFT' )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:781:11: 'LEFT'
            {
            match("LEFT"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end KW_LEFT

    // $ANTLR start KW_RIGHT
    public final void mKW_RIGHT() throws RecognitionException {
        try {
            int _type = KW_RIGHT;
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:782:10: ( 'RIGHT' )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:782:12: 'RIGHT'
            {
            match("RIGHT"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end KW_RIGHT

    // $ANTLR start KW_FULL
    public final void mKW_FULL() throws RecognitionException {
        try {
            int _type = KW_FULL;
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:783:9: ( 'FULL' )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:783:11: 'FULL'
            {
            match("FULL"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end KW_FULL

    // $ANTLR start KW_ON
    public final void mKW_ON() throws RecognitionException {
        try {
            int _type = KW_ON;
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:784:7: ( 'ON' )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:784:9: 'ON'
            {
            match("ON"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end KW_ON

    // $ANTLR start KW_PARTITION
    public final void mKW_PARTITION() throws RecognitionException {
        try {
            int _type = KW_PARTITION;
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:785:14: ( 'PARTITION' )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:785:16: 'PARTITION'
            {
            match("PARTITION"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end KW_PARTITION

    // $ANTLR start KW_PARTITIONS
    public final void mKW_PARTITIONS() throws RecognitionException {
        try {
            int _type = KW_PARTITIONS;
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:786:15: ( 'PARTITIONS' )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:786:17: 'PARTITIONS'
            {
            match("PARTITIONS"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end KW_PARTITIONS

    // $ANTLR start KW_TABLE
    public final void mKW_TABLE() throws RecognitionException {
        try {
            int _type = KW_TABLE;
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:787:9: ( 'TABLE' )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:787:11: 'TABLE'
            {
            match("TABLE"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end KW_TABLE

    // $ANTLR start KW_TABLES
    public final void mKW_TABLES() throws RecognitionException {
        try {
            int _type = KW_TABLES;
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:788:10: ( 'TABLES' )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:788:12: 'TABLES'
            {
            match("TABLES"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end KW_TABLES

    // $ANTLR start KW_SHOW
    public final void mKW_SHOW() throws RecognitionException {
        try {
            int _type = KW_SHOW;
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:789:8: ( 'SHOW' )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:789:10: 'SHOW'
            {
            match("SHOW"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end KW_SHOW

    // $ANTLR start KW_DIRECTORY
    public final void mKW_DIRECTORY() throws RecognitionException {
        try {
            int _type = KW_DIRECTORY;
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:790:13: ( 'DIRECTORY' )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:790:15: 'DIRECTORY'
            {
            match("DIRECTORY"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end KW_DIRECTORY

    // $ANTLR start KW_LOCAL
    public final void mKW_LOCAL() throws RecognitionException {
        try {
            int _type = KW_LOCAL;
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:791:9: ( 'LOCAL' )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:791:11: 'LOCAL'
            {
            match("LOCAL"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end KW_LOCAL

    // $ANTLR start KW_TRANSFORM
    public final void mKW_TRANSFORM() throws RecognitionException {
        try {
            int _type = KW_TRANSFORM;
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:792:14: ( 'TRANSFORM' )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:792:16: 'TRANSFORM'
            {
            match("TRANSFORM"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end KW_TRANSFORM

    // $ANTLR start KW_USING
    public final void mKW_USING() throws RecognitionException {
        try {
            int _type = KW_USING;
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:793:9: ( 'USING' )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:793:11: 'USING'
            {
            match("USING"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end KW_USING

    // $ANTLR start KW_CLUSTER
    public final void mKW_CLUSTER() throws RecognitionException {
        try {
            int _type = KW_CLUSTER;
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:794:11: ( 'CLUSTER' )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:794:13: 'CLUSTER'
            {
            match("CLUSTER"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end KW_CLUSTER

    // $ANTLR start KW_UNION
    public final void mKW_UNION() throws RecognitionException {
        try {
            int _type = KW_UNION;
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:795:9: ( 'UNION' )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:795:11: 'UNION'
            {
            match("UNION"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end KW_UNION

    // $ANTLR start KW_LOAD
    public final void mKW_LOAD() throws RecognitionException {
        try {
            int _type = KW_LOAD;
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:796:8: ( 'LOAD' )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:796:10: 'LOAD'
            {
            match("LOAD"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end KW_LOAD

    // $ANTLR start KW_DATA
    public final void mKW_DATA() throws RecognitionException {
        try {
            int _type = KW_DATA;
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:797:8: ( 'DATA' )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:797:10: 'DATA'
            {
            match("DATA"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end KW_DATA

    // $ANTLR start KW_INPATH
    public final void mKW_INPATH() throws RecognitionException {
        try {
            int _type = KW_INPATH;
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:798:10: ( 'INPATH' )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:798:12: 'INPATH'
            {
            match("INPATH"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end KW_INPATH

    // $ANTLR start KW_IS
    public final void mKW_IS() throws RecognitionException {
        try {
            int _type = KW_IS;
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:799:6: ( 'IS' )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:799:8: 'IS'
            {
            match("IS"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end KW_IS

    // $ANTLR start KW_NULL
    public final void mKW_NULL() throws RecognitionException {
        try {
            int _type = KW_NULL;
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:800:8: ( 'NULL' )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:800:10: 'NULL'
            {
            match("NULL"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end KW_NULL

    // $ANTLR start KW_CREATE
    public final void mKW_CREATE() throws RecognitionException {
        try {
            int _type = KW_CREATE;
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:801:10: ( 'CREATE' )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:801:12: 'CREATE'
            {
            match("CREATE"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end KW_CREATE

    // $ANTLR start KW_EXTERNAL
    public final void mKW_EXTERNAL() throws RecognitionException {
        try {
            int _type = KW_EXTERNAL;
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:802:12: ( 'EXTERNAL' )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:802:14: 'EXTERNAL'
            {
            match("EXTERNAL"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end KW_EXTERNAL

    // $ANTLR start KW_ALTER
    public final void mKW_ALTER() throws RecognitionException {
        try {
            int _type = KW_ALTER;
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:803:9: ( 'ALTER' )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:803:11: 'ALTER'
            {
            match("ALTER"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end KW_ALTER

    // $ANTLR start KW_DESCRIBE
    public final void mKW_DESCRIBE() throws RecognitionException {
        try {
            int _type = KW_DESCRIBE;
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:804:12: ( 'DESCRIBE' )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:804:14: 'DESCRIBE'
            {
            match("DESCRIBE"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end KW_DESCRIBE

    // $ANTLR start KW_DROP
    public final void mKW_DROP() throws RecognitionException {
        try {
            int _type = KW_DROP;
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:805:8: ( 'DROP' )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:805:10: 'DROP'
            {
            match("DROP"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end KW_DROP

    // $ANTLR start KW_RENAME
    public final void mKW_RENAME() throws RecognitionException {
        try {
            int _type = KW_RENAME;
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:806:10: ( 'RENAME' )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:806:12: 'RENAME'
            {
            match("RENAME"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end KW_RENAME

    // $ANTLR start KW_TO
    public final void mKW_TO() throws RecognitionException {
        try {
            int _type = KW_TO;
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:807:6: ( 'TO' )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:807:8: 'TO'
            {
            match("TO"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end KW_TO

    // $ANTLR start KW_COMMENT
    public final void mKW_COMMENT() throws RecognitionException {
        try {
            int _type = KW_COMMENT;
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:808:11: ( 'COMMENT' )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:808:13: 'COMMENT'
            {
            match("COMMENT"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end KW_COMMENT

    // $ANTLR start KW_BOOLEAN
    public final void mKW_BOOLEAN() throws RecognitionException {
        try {
            int _type = KW_BOOLEAN;
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:809:11: ( 'BOOLEAN' )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:809:13: 'BOOLEAN'
            {
            match("BOOLEAN"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end KW_BOOLEAN

    // $ANTLR start KW_TINYINT
    public final void mKW_TINYINT() throws RecognitionException {
        try {
            int _type = KW_TINYINT;
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:810:11: ( 'TINYINT' )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:810:13: 'TINYINT'
            {
            match("TINYINT"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end KW_TINYINT

    // $ANTLR start KW_INT
    public final void mKW_INT() throws RecognitionException {
        try {
            int _type = KW_INT;
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:811:7: ( 'INT' )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:811:9: 'INT'
            {
            match("INT"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end KW_INT

    // $ANTLR start KW_BIGINT
    public final void mKW_BIGINT() throws RecognitionException {
        try {
            int _type = KW_BIGINT;
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:812:10: ( 'BIGINT' )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:812:12: 'BIGINT'
            {
            match("BIGINT"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end KW_BIGINT

    // $ANTLR start KW_FLOAT
    public final void mKW_FLOAT() throws RecognitionException {
        try {
            int _type = KW_FLOAT;
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:813:9: ( 'FLOAT' )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:813:11: 'FLOAT'
            {
            match("FLOAT"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end KW_FLOAT

    // $ANTLR start KW_DOUBLE
    public final void mKW_DOUBLE() throws RecognitionException {
        try {
            int _type = KW_DOUBLE;
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:814:10: ( 'DOUBLE' )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:814:12: 'DOUBLE'
            {
            match("DOUBLE"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end KW_DOUBLE

    // $ANTLR start KW_DATE
    public final void mKW_DATE() throws RecognitionException {
        try {
            int _type = KW_DATE;
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:815:8: ( 'DATE' )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:815:10: 'DATE'
            {
            match("DATE"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end KW_DATE

    // $ANTLR start KW_DATETIME
    public final void mKW_DATETIME() throws RecognitionException {
        try {
            int _type = KW_DATETIME;
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:816:12: ( 'DATETIME' )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:816:14: 'DATETIME'
            {
            match("DATETIME"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end KW_DATETIME

    // $ANTLR start KW_TIMESTAMP
    public final void mKW_TIMESTAMP() throws RecognitionException {
        try {
            int _type = KW_TIMESTAMP;
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:817:13: ( 'TIMESTAMP' )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:817:15: 'TIMESTAMP'
            {
            match("TIMESTAMP"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end KW_TIMESTAMP

    // $ANTLR start KW_STRING
    public final void mKW_STRING() throws RecognitionException {
        try {
            int _type = KW_STRING;
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:818:10: ( 'STRING' )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:818:12: 'STRING'
            {
            match("STRING"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end KW_STRING

    // $ANTLR start KW_ARRAY
    public final void mKW_ARRAY() throws RecognitionException {
        try {
            int _type = KW_ARRAY;
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:819:9: ( 'ARRAY' )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:819:11: 'ARRAY'
            {
            match("ARRAY"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end KW_ARRAY

    // $ANTLR start KW_MAP
    public final void mKW_MAP() throws RecognitionException {
        try {
            int _type = KW_MAP;
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:820:7: ( 'MAP' )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:820:9: 'MAP'
            {
            match("MAP"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end KW_MAP

    // $ANTLR start KW_PARTITIONED
    public final void mKW_PARTITIONED() throws RecognitionException {
        try {
            int _type = KW_PARTITIONED;
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:821:15: ( 'PARTITIONED' )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:821:17: 'PARTITIONED'
            {
            match("PARTITIONED"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end KW_PARTITIONED

    // $ANTLR start KW_CLUSTERED
    public final void mKW_CLUSTERED() throws RecognitionException {
        try {
            int _type = KW_CLUSTERED;
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:822:13: ( 'CLUSTERED' )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:822:15: 'CLUSTERED'
            {
            match("CLUSTERED"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end KW_CLUSTERED

    // $ANTLR start KW_SORTED
    public final void mKW_SORTED() throws RecognitionException {
        try {
            int _type = KW_SORTED;
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:823:10: ( 'SORTED' )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:823:12: 'SORTED'
            {
            match("SORTED"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end KW_SORTED

    // $ANTLR start KW_INTO
    public final void mKW_INTO() throws RecognitionException {
        try {
            int _type = KW_INTO;
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:824:8: ( 'INTO' )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:824:10: 'INTO'
            {
            match("INTO"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end KW_INTO

    // $ANTLR start KW_BUCKETS
    public final void mKW_BUCKETS() throws RecognitionException {
        try {
            int _type = KW_BUCKETS;
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:825:11: ( 'BUCKETS' )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:825:13: 'BUCKETS'
            {
            match("BUCKETS"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end KW_BUCKETS

    // $ANTLR start KW_ROW
    public final void mKW_ROW() throws RecognitionException {
        try {
            int _type = KW_ROW;
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:826:7: ( 'ROW' )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:826:9: 'ROW'
            {
            match("ROW"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end KW_ROW

    // $ANTLR start KW_FORMAT
    public final void mKW_FORMAT() throws RecognitionException {
        try {
            int _type = KW_FORMAT;
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:827:10: ( 'FORMAT' )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:827:12: 'FORMAT'
            {
            match("FORMAT"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end KW_FORMAT

    // $ANTLR start KW_DELIMITED
    public final void mKW_DELIMITED() throws RecognitionException {
        try {
            int _type = KW_DELIMITED;
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:828:13: ( 'DELIMITED' )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:828:15: 'DELIMITED'
            {
            match("DELIMITED"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end KW_DELIMITED

    // $ANTLR start KW_FIELDS
    public final void mKW_FIELDS() throws RecognitionException {
        try {
            int _type = KW_FIELDS;
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:829:10: ( 'FIELDS' )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:829:12: 'FIELDS'
            {
            match("FIELDS"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end KW_FIELDS

    // $ANTLR start KW_TERMINATED
    public final void mKW_TERMINATED() throws RecognitionException {
        try {
            int _type = KW_TERMINATED;
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:830:14: ( 'TERMINATED' )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:830:16: 'TERMINATED'
            {
            match("TERMINATED"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end KW_TERMINATED

    // $ANTLR start KW_COLLECTION
    public final void mKW_COLLECTION() throws RecognitionException {
        try {
            int _type = KW_COLLECTION;
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:831:14: ( 'COLLECTION' )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:831:16: 'COLLECTION'
            {
            match("COLLECTION"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end KW_COLLECTION

    // $ANTLR start KW_ITEMS
    public final void mKW_ITEMS() throws RecognitionException {
        try {
            int _type = KW_ITEMS;
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:832:9: ( 'ITEMS' )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:832:11: 'ITEMS'
            {
            match("ITEMS"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end KW_ITEMS

    // $ANTLR start KW_KEYS
    public final void mKW_KEYS() throws RecognitionException {
        try {
            int _type = KW_KEYS;
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:833:8: ( 'KEYS' )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:833:10: 'KEYS'
            {
            match("KEYS"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end KW_KEYS

    // $ANTLR start KW_LINES
    public final void mKW_LINES() throws RecognitionException {
        try {
            int _type = KW_LINES;
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:834:9: ( 'LINES' )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:834:11: 'LINES'
            {
            match("LINES"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end KW_LINES

    // $ANTLR start KW_STORED
    public final void mKW_STORED() throws RecognitionException {
        try {
            int _type = KW_STORED;
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:835:10: ( 'STORED' )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:835:12: 'STORED'
            {
            match("STORED"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end KW_STORED

    // $ANTLR start KW_SEQUENCEFILE
    public final void mKW_SEQUENCEFILE() throws RecognitionException {
        try {
            int _type = KW_SEQUENCEFILE;
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:836:16: ( 'SEQUENCEFILE' )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:836:18: 'SEQUENCEFILE'
            {
            match("SEQUENCEFILE"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end KW_SEQUENCEFILE

    // $ANTLR start KW_LOCATION
    public final void mKW_LOCATION() throws RecognitionException {
        try {
            int _type = KW_LOCATION;
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:837:12: ( 'LOCATION' )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:837:14: 'LOCATION'
            {
            match("LOCATION"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end KW_LOCATION

    // $ANTLR start KW_TABLESAMPLE
    public final void mKW_TABLESAMPLE() throws RecognitionException {
        try {
            int _type = KW_TABLESAMPLE;
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:838:15: ( 'TABLESAMPLE' )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:838:17: 'TABLESAMPLE'
            {
            match("TABLESAMPLE"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end KW_TABLESAMPLE

    // $ANTLR start KW_BUCKET
    public final void mKW_BUCKET() throws RecognitionException {
        try {
            int _type = KW_BUCKET;
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:839:10: ( 'BUCKET' )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:839:12: 'BUCKET'
            {
            match("BUCKET"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end KW_BUCKET

    // $ANTLR start KW_OUT
    public final void mKW_OUT() throws RecognitionException {
        try {
            int _type = KW_OUT;
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:840:7: ( 'OUT' )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:840:9: 'OUT'
            {
            match("OUT"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end KW_OUT

    // $ANTLR start KW_OF
    public final void mKW_OF() throws RecognitionException {
        try {
            int _type = KW_OF;
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:841:6: ( 'OF' )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:841:8: 'OF'
            {
            match("OF"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end KW_OF

    // $ANTLR start KW_CAST
    public final void mKW_CAST() throws RecognitionException {
        try {
            int _type = KW_CAST;
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:842:8: ( 'CAST' )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:842:10: 'CAST'
            {
            match("CAST"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end KW_CAST

    // $ANTLR start KW_ADD
    public final void mKW_ADD() throws RecognitionException {
        try {
            int _type = KW_ADD;
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:843:7: ( 'ADD' )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:843:9: 'ADD'
            {
            match("ADD"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end KW_ADD

    // $ANTLR start KW_REPLACE
    public final void mKW_REPLACE() throws RecognitionException {
        try {
            int _type = KW_REPLACE;
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:844:11: ( 'REPLACE' )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:844:13: 'REPLACE'
            {
            match("REPLACE"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end KW_REPLACE

    // $ANTLR start KW_COLUMNS
    public final void mKW_COLUMNS() throws RecognitionException {
        try {
            int _type = KW_COLUMNS;
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:845:11: ( 'COLUMNS' )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:845:13: 'COLUMNS'
            {
            match("COLUMNS"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end KW_COLUMNS

    // $ANTLR start KW_RLIKE
    public final void mKW_RLIKE() throws RecognitionException {
        try {
            int _type = KW_RLIKE;
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:846:9: ( 'RLIKE' )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:846:11: 'RLIKE'
            {
            match("RLIKE"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end KW_RLIKE

    // $ANTLR start KW_REGEXP
    public final void mKW_REGEXP() throws RecognitionException {
        try {
            int _type = KW_REGEXP;
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:847:10: ( 'REGEXP' )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:847:12: 'REGEXP'
            {
            match("REGEXP"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end KW_REGEXP

    // $ANTLR start KW_TEMPORARY
    public final void mKW_TEMPORARY() throws RecognitionException {
        try {
            int _type = KW_TEMPORARY;
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:848:13: ( 'TEMPORARY' )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:848:15: 'TEMPORARY'
            {
            match("TEMPORARY"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end KW_TEMPORARY

    // $ANTLR start KW_FUNCTION
    public final void mKW_FUNCTION() throws RecognitionException {
        try {
            int _type = KW_FUNCTION;
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:849:12: ( 'FUNCTION' )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:849:14: 'FUNCTION'
            {
            match("FUNCTION"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end KW_FUNCTION

    // $ANTLR start KW_EXPLAIN
    public final void mKW_EXPLAIN() throws RecognitionException {
        try {
            int _type = KW_EXPLAIN;
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:850:11: ( 'EXPLAIN' )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:850:13: 'EXPLAIN'
            {
            match("EXPLAIN"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end KW_EXPLAIN

    // $ANTLR start KW_EXTENDED
    public final void mKW_EXTENDED() throws RecognitionException {
        try {
            int _type = KW_EXTENDED;
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:851:12: ( 'EXTENDED' )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:851:14: 'EXTENDED'
            {
            match("EXTENDED"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end KW_EXTENDED

    // $ANTLR start KW_SERIALIZER
    public final void mKW_SERIALIZER() throws RecognitionException {
        try {
            int _type = KW_SERIALIZER;
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:852:14: ( 'SERIALIZER' )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:852:16: 'SERIALIZER'
            {
            match("SERIALIZER"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end KW_SERIALIZER

    // $ANTLR start KW_WITH
    public final void mKW_WITH() throws RecognitionException {
        try {
            int _type = KW_WITH;
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:853:8: ( 'WITH' )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:853:10: 'WITH'
            {
            match("WITH"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end KW_WITH

    // $ANTLR start KW_PROPERTIES
    public final void mKW_PROPERTIES() throws RecognitionException {
        try {
            int _type = KW_PROPERTIES;
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:854:14: ( 'SERDEPROPERTIES' )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:854:16: 'SERDEPROPERTIES'
            {
            match("SERDEPROPERTIES"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end KW_PROPERTIES

    // $ANTLR start KW_LIMIT
    public final void mKW_LIMIT() throws RecognitionException {
        try {
            int _type = KW_LIMIT;
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:855:9: ( 'LIMIT' )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:855:11: 'LIMIT'
            {
            match("LIMIT"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end KW_LIMIT

    // $ANTLR start DOT
    public final void mDOT() throws RecognitionException {
        try {
            int _type = DOT;
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:859:5: ( '.' )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:859:7: '.'
            {
            match('.'); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end DOT

    // $ANTLR start COLON
    public final void mCOLON() throws RecognitionException {
        try {
            int _type = COLON;
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:860:7: ( ':' )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:860:9: ':'
            {
            match(':'); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end COLON

    // $ANTLR start COMMA
    public final void mCOMMA() throws RecognitionException {
        try {
            int _type = COMMA;
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:861:7: ( ',' )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:861:9: ','
            {
            match(','); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end COMMA

    // $ANTLR start SEMICOLON
    public final void mSEMICOLON() throws RecognitionException {
        try {
            int _type = SEMICOLON;
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:862:11: ( ';' )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:862:13: ';'
            {
            match(';'); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end SEMICOLON

    // $ANTLR start LPAREN
    public final void mLPAREN() throws RecognitionException {
        try {
            int _type = LPAREN;
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:864:8: ( '(' )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:864:10: '('
            {
            match('('); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end LPAREN

    // $ANTLR start RPAREN
    public final void mRPAREN() throws RecognitionException {
        try {
            int _type = RPAREN;
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:865:8: ( ')' )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:865:10: ')'
            {
            match(')'); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end RPAREN

    // $ANTLR start LSQUARE
    public final void mLSQUARE() throws RecognitionException {
        try {
            int _type = LSQUARE;
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:866:9: ( '[' )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:866:11: '['
            {
            match('['); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end LSQUARE

    // $ANTLR start RSQUARE
    public final void mRSQUARE() throws RecognitionException {
        try {
            int _type = RSQUARE;
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:867:9: ( ']' )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:867:11: ']'
            {
            match(']'); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end RSQUARE

    // $ANTLR start EQUAL
    public final void mEQUAL() throws RecognitionException {
        try {
            int _type = EQUAL;
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:869:7: ( '=' )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:869:9: '='
            {
            match('='); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end EQUAL

    // $ANTLR start NOTEQUAL
    public final void mNOTEQUAL() throws RecognitionException {
        try {
            int _type = NOTEQUAL;
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:870:10: ( '<>' )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:870:12: '<>'
            {
            match("<>"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end NOTEQUAL

    // $ANTLR start LESSTHANOREQUALTO
    public final void mLESSTHANOREQUALTO() throws RecognitionException {
        try {
            int _type = LESSTHANOREQUALTO;
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:871:19: ( '<=' )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:871:21: '<='
            {
            match("<="); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end LESSTHANOREQUALTO

    // $ANTLR start LESSTHAN
    public final void mLESSTHAN() throws RecognitionException {
        try {
            int _type = LESSTHAN;
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:872:10: ( '<' )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:872:12: '<'
            {
            match('<'); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end LESSTHAN

    // $ANTLR start GREATERTHANOREQUALTO
    public final void mGREATERTHANOREQUALTO() throws RecognitionException {
        try {
            int _type = GREATERTHANOREQUALTO;
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:873:22: ( '>=' )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:873:24: '>='
            {
            match(">="); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end GREATERTHANOREQUALTO

    // $ANTLR start GREATERTHAN
    public final void mGREATERTHAN() throws RecognitionException {
        try {
            int _type = GREATERTHAN;
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:874:13: ( '>' )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:874:15: '>'
            {
            match('>'); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end GREATERTHAN

    // $ANTLR start DIVIDE
    public final void mDIVIDE() throws RecognitionException {
        try {
            int _type = DIVIDE;
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:876:8: ( '/' )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:876:10: '/'
            {
            match('/'); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end DIVIDE

    // $ANTLR start PLUS
    public final void mPLUS() throws RecognitionException {
        try {
            int _type = PLUS;
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:877:6: ( '+' )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:877:8: '+'
            {
            match('+'); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end PLUS

    // $ANTLR start MINUS
    public final void mMINUS() throws RecognitionException {
        try {
            int _type = MINUS;
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:878:7: ( '-' )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:878:9: '-'
            {
            match('-'); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end MINUS

    // $ANTLR start STAR
    public final void mSTAR() throws RecognitionException {
        try {
            int _type = STAR;
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:879:6: ( '*' )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:879:8: '*'
            {
            match('*'); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end STAR

    // $ANTLR start MOD
    public final void mMOD() throws RecognitionException {
        try {
            int _type = MOD;
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:880:5: ( '%' )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:880:7: '%'
            {
            match('%'); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end MOD

    // $ANTLR start AMPERSAND
    public final void mAMPERSAND() throws RecognitionException {
        try {
            int _type = AMPERSAND;
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:882:11: ( '&' )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:882:13: '&'
            {
            match('&'); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end AMPERSAND

    // $ANTLR start TILDE
    public final void mTILDE() throws RecognitionException {
        try {
            int _type = TILDE;
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:883:7: ( '~' )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:883:9: '~'
            {
            match('~'); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end TILDE

    // $ANTLR start BITWISEOR
    public final void mBITWISEOR() throws RecognitionException {
        try {
            int _type = BITWISEOR;
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:884:11: ( '|' )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:884:13: '|'
            {
            match('|'); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end BITWISEOR

    // $ANTLR start BITWISEXOR
    public final void mBITWISEXOR() throws RecognitionException {
        try {
            int _type = BITWISEXOR;
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:885:12: ( '^' )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:885:14: '^'
            {
            match('^'); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end BITWISEXOR

    // $ANTLR start Letter
    public final void mLetter() throws RecognitionException {
        try {
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:890:5: ( 'a' .. 'z' | 'A' .. 'Z' )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:
            {
            if ( (input.LA(1)>='A' && input.LA(1)<='Z')||(input.LA(1)>='a' && input.LA(1)<='z') ) {
                input.consume();

            }
            else {
                MismatchedSetException mse =
                    new MismatchedSetException(null,input);
                recover(mse);    throw mse;
            }


            }

        }
        finally {
        }
    }
    // $ANTLR end Letter

    // $ANTLR start HexDigit
    public final void mHexDigit() throws RecognitionException {
        try {
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:895:5: ( 'a' .. 'f' | 'A' .. 'F' )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:
            {
            if ( (input.LA(1)>='A' && input.LA(1)<='F')||(input.LA(1)>='a' && input.LA(1)<='f') ) {
                input.consume();

            }
            else {
                MismatchedSetException mse =
                    new MismatchedSetException(null,input);
                recover(mse);    throw mse;
            }


            }

        }
        finally {
        }
    }
    // $ANTLR end HexDigit

    // $ANTLR start Digit
    public final void mDigit() throws RecognitionException {
        try {
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:900:5: ( '0' .. '9' )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:901:5: '0' .. '9'
            {
            matchRange('0','9'); 

            }

        }
        finally {
        }
    }
    // $ANTLR end Digit

    // $ANTLR start Exponent
    public final void mExponent() throws RecognitionException {
        try {
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:906:5: ( 'e' ( PLUS | MINUS )? ( Digit )+ )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:907:5: 'e' ( PLUS | MINUS )? ( Digit )+
            {
            match('e'); 
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:907:9: ( PLUS | MINUS )?
            int alt1=2;
            int LA1_0 = input.LA(1);

            if ( (LA1_0=='+'||LA1_0=='-') ) {
                alt1=1;
            }
            switch (alt1) {
                case 1 :
                    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:
                    {
                    if ( input.LA(1)=='+'||input.LA(1)=='-' ) {
                        input.consume();

                    }
                    else {
                        MismatchedSetException mse =
                            new MismatchedSetException(null,input);
                        recover(mse);    throw mse;
                    }


                    }
                    break;

            }

            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:907:25: ( Digit )+
            int cnt2=0;
            loop2:
            do {
                int alt2=2;
                int LA2_0 = input.LA(1);

                if ( ((LA2_0>='0' && LA2_0<='9')) ) {
                    alt2=1;
                }


                switch (alt2) {
            	case 1 :
            	    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:907:26: Digit
            	    {
            	    mDigit(); 

            	    }
            	    break;

            	default :
            	    if ( cnt2 >= 1 ) break loop2;
                        EarlyExitException eee =
                            new EarlyExitException(2, input);
                        throw eee;
                }
                cnt2++;
            } while (true);


            }

        }
        finally {
        }
    }
    // $ANTLR end Exponent

    // $ANTLR start StringLiteral
    public final void mStringLiteral() throws RecognitionException {
        try {
            int _type = StringLiteral;
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:911:5: ( '\\'' (~ '\\'' )* '\\'' ( '\\'' (~ '\\'' )* '\\'' )* )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:912:5: '\\'' (~ '\\'' )* '\\'' ( '\\'' (~ '\\'' )* '\\'' )*
            {
            match('\''); 
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:912:10: (~ '\\'' )*
            loop3:
            do {
                int alt3=2;
                int LA3_0 = input.LA(1);

                if ( ((LA3_0>='\u0000' && LA3_0<='&')||(LA3_0>='(' && LA3_0<='\uFFFE')) ) {
                    alt3=1;
                }


                switch (alt3) {
            	case 1 :
            	    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:912:11: ~ '\\''
            	    {
            	    if ( (input.LA(1)>='\u0000' && input.LA(1)<='&')||(input.LA(1)>='(' && input.LA(1)<='\uFFFE') ) {
            	        input.consume();

            	    }
            	    else {
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recover(mse);    throw mse;
            	    }


            	    }
            	    break;

            	default :
            	    break loop3;
                }
            } while (true);

            match('\''); 
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:912:24: ( '\\'' (~ '\\'' )* '\\'' )*
            loop5:
            do {
                int alt5=2;
                int LA5_0 = input.LA(1);

                if ( (LA5_0=='\'') ) {
                    alt5=1;
                }


                switch (alt5) {
            	case 1 :
            	    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:912:26: '\\'' (~ '\\'' )* '\\''
            	    {
            	    match('\''); 
            	    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:912:31: (~ '\\'' )*
            	    loop4:
            	    do {
            	        int alt4=2;
            	        int LA4_0 = input.LA(1);

            	        if ( ((LA4_0>='\u0000' && LA4_0<='&')||(LA4_0>='(' && LA4_0<='\uFFFE')) ) {
            	            alt4=1;
            	        }


            	        switch (alt4) {
            	    	case 1 :
            	    	    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:912:32: ~ '\\''
            	    	    {
            	    	    if ( (input.LA(1)>='\u0000' && input.LA(1)<='&')||(input.LA(1)>='(' && input.LA(1)<='\uFFFE') ) {
            	    	        input.consume();

            	    	    }
            	    	    else {
            	    	        MismatchedSetException mse =
            	    	            new MismatchedSetException(null,input);
            	    	        recover(mse);    throw mse;
            	    	    }


            	    	    }
            	    	    break;

            	    	default :
            	    	    break loop4;
            	        }
            	    } while (true);

            	    match('\''); 

            	    }
            	    break;

            	default :
            	    break loop5;
                }
            } while (true);


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end StringLiteral

    // $ANTLR start CharSetLiteral
    public final void mCharSetLiteral() throws RecognitionException {
        try {
            int _type = CharSetLiteral;
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:916:5: ( StringLiteral | '0' 'X' ( HexDigit | Digit )+ )
            int alt7=2;
            int LA7_0 = input.LA(1);

            if ( (LA7_0=='\'') ) {
                alt7=1;
            }
            else if ( (LA7_0=='0') ) {
                alt7=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("915:1: CharSetLiteral : ( StringLiteral | '0' 'X' ( HexDigit | Digit )+ );", 7, 0, input);

                throw nvae;
            }
            switch (alt7) {
                case 1 :
                    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:917:5: StringLiteral
                    {
                    mStringLiteral(); 

                    }
                    break;
                case 2 :
                    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:918:7: '0' 'X' ( HexDigit | Digit )+
                    {
                    match('0'); 
                    match('X'); 
                    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:918:15: ( HexDigit | Digit )+
                    int cnt6=0;
                    loop6:
                    do {
                        int alt6=2;
                        int LA6_0 = input.LA(1);

                        if ( ((LA6_0>='0' && LA6_0<='9')||(LA6_0>='A' && LA6_0<='F')||(LA6_0>='a' && LA6_0<='f')) ) {
                            alt6=1;
                        }


                        switch (alt6) {
                    	case 1 :
                    	    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:
                    	    {
                    	    if ( (input.LA(1)>='0' && input.LA(1)<='9')||(input.LA(1)>='A' && input.LA(1)<='F')||(input.LA(1)>='a' && input.LA(1)<='f') ) {
                    	        input.consume();

                    	    }
                    	    else {
                    	        MismatchedSetException mse =
                    	            new MismatchedSetException(null,input);
                    	        recover(mse);    throw mse;
                    	    }


                    	    }
                    	    break;

                    	default :
                    	    if ( cnt6 >= 1 ) break loop6;
                                EarlyExitException eee =
                                    new EarlyExitException(6, input);
                                throw eee;
                        }
                        cnt6++;
                    } while (true);


                    }
                    break;

            }
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end CharSetLiteral

    // $ANTLR start Number
    public final void mNumber() throws RecognitionException {
        try {
            int _type = Number;
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:922:5: ( ( Digit )+ ( DOT ( Digit )* ( Exponent )? | Exponent )? )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:923:5: ( Digit )+ ( DOT ( Digit )* ( Exponent )? | Exponent )?
            {
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:923:5: ( Digit )+
            int cnt8=0;
            loop8:
            do {
                int alt8=2;
                int LA8_0 = input.LA(1);

                if ( ((LA8_0>='0' && LA8_0<='9')) ) {
                    alt8=1;
                }


                switch (alt8) {
            	case 1 :
            	    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:923:6: Digit
            	    {
            	    mDigit(); 

            	    }
            	    break;

            	default :
            	    if ( cnt8 >= 1 ) break loop8;
                        EarlyExitException eee =
                            new EarlyExitException(8, input);
                        throw eee;
                }
                cnt8++;
            } while (true);

            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:923:14: ( DOT ( Digit )* ( Exponent )? | Exponent )?
            int alt11=3;
            int LA11_0 = input.LA(1);

            if ( (LA11_0=='.') ) {
                alt11=1;
            }
            else if ( (LA11_0=='e') ) {
                alt11=2;
            }
            switch (alt11) {
                case 1 :
                    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:923:16: DOT ( Digit )* ( Exponent )?
                    {
                    mDOT(); 
                    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:923:20: ( Digit )*
                    loop9:
                    do {
                        int alt9=2;
                        int LA9_0 = input.LA(1);

                        if ( ((LA9_0>='0' && LA9_0<='9')) ) {
                            alt9=1;
                        }


                        switch (alt9) {
                    	case 1 :
                    	    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:923:21: Digit
                    	    {
                    	    mDigit(); 

                    	    }
                    	    break;

                    	default :
                    	    break loop9;
                        }
                    } while (true);

                    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:923:29: ( Exponent )?
                    int alt10=2;
                    int LA10_0 = input.LA(1);

                    if ( (LA10_0=='e') ) {
                        alt10=1;
                    }
                    switch (alt10) {
                        case 1 :
                            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:923:30: Exponent
                            {
                            mExponent(); 

                            }
                            break;

                    }


                    }
                    break;
                case 2 :
                    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:923:43: Exponent
                    {
                    mExponent(); 

                    }
                    break;

            }


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end Number

    // $ANTLR start Identifier
    public final void mIdentifier() throws RecognitionException {
        try {
            int _type = Identifier;
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:927:5: ( ( Letter | Digit ) ( Letter | Digit | '_' )* )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:928:5: ( Letter | Digit ) ( Letter | Digit | '_' )*
            {
            if ( (input.LA(1)>='0' && input.LA(1)<='9')||(input.LA(1)>='A' && input.LA(1)<='Z')||(input.LA(1)>='a' && input.LA(1)<='z') ) {
                input.consume();

            }
            else {
                MismatchedSetException mse =
                    new MismatchedSetException(null,input);
                recover(mse);    throw mse;
            }

            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:928:22: ( Letter | Digit | '_' )*
            loop12:
            do {
                int alt12=2;
                int LA12_0 = input.LA(1);

                if ( ((LA12_0>='0' && LA12_0<='9')||(LA12_0>='A' && LA12_0<='Z')||LA12_0=='_'||(LA12_0>='a' && LA12_0<='z')) ) {
                    alt12=1;
                }


                switch (alt12) {
            	case 1 :
            	    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:
            	    {
            	    if ( (input.LA(1)>='0' && input.LA(1)<='9')||(input.LA(1)>='A' && input.LA(1)<='Z')||input.LA(1)=='_'||(input.LA(1)>='a' && input.LA(1)<='z') ) {
            	        input.consume();

            	    }
            	    else {
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recover(mse);    throw mse;
            	    }


            	    }
            	    break;

            	default :
            	    break loop12;
                }
            } while (true);


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end Identifier

    // $ANTLR start CharSetName
    public final void mCharSetName() throws RecognitionException {
        try {
            int _type = CharSetName;
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:932:5: ( '_' ( Letter | Digit | '_' | '-' | '.' | ':' )+ )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:933:5: '_' ( Letter | Digit | '_' | '-' | '.' | ':' )+
            {
            match('_'); 
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:933:9: ( Letter | Digit | '_' | '-' | '.' | ':' )+
            int cnt13=0;
            loop13:
            do {
                int alt13=2;
                int LA13_0 = input.LA(1);

                if ( ((LA13_0>='-' && LA13_0<='.')||(LA13_0>='0' && LA13_0<=':')||(LA13_0>='A' && LA13_0<='Z')||LA13_0=='_'||(LA13_0>='a' && LA13_0<='z')) ) {
                    alt13=1;
                }


                switch (alt13) {
            	case 1 :
            	    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:
            	    {
            	    if ( (input.LA(1)>='-' && input.LA(1)<='.')||(input.LA(1)>='0' && input.LA(1)<=':')||(input.LA(1)>='A' && input.LA(1)<='Z')||input.LA(1)=='_'||(input.LA(1)>='a' && input.LA(1)<='z') ) {
            	        input.consume();

            	    }
            	    else {
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recover(mse);    throw mse;
            	    }


            	    }
            	    break;

            	default :
            	    if ( cnt13 >= 1 ) break loop13;
                        EarlyExitException eee =
                            new EarlyExitException(13, input);
                        throw eee;
                }
                cnt13++;
            } while (true);


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end CharSetName

    // $ANTLR start WS
    public final void mWS() throws RecognitionException {
        try {
            int _type = WS;
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:936:5: ( ( ' ' | '\\r' | '\\t' | '\\n' ) )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:936:8: ( ' ' | '\\r' | '\\t' | '\\n' )
            {
            if ( (input.LA(1)>='\t' && input.LA(1)<='\n')||input.LA(1)=='\r'||input.LA(1)==' ' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse =
                    new MismatchedSetException(null,input);
                recover(mse);    throw mse;
            }

            channel=HIDDEN;

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end WS

    // $ANTLR start COMMENT
    public final void mCOMMENT() throws RecognitionException {
        try {
            int _type = COMMENT;
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:940:3: ( '--' (~ ( '\\n' | '\\r' ) )* )
            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:940:5: '--' (~ ( '\\n' | '\\r' ) )*
            {
            match("--"); 

            // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:940:10: (~ ( '\\n' | '\\r' ) )*
            loop14:
            do {
                int alt14=2;
                int LA14_0 = input.LA(1);

                if ( ((LA14_0>='\u0000' && LA14_0<='\t')||(LA14_0>='\u000B' && LA14_0<='\f')||(LA14_0>='\u000E' && LA14_0<='\uFFFE')) ) {
                    alt14=1;
                }


                switch (alt14) {
            	case 1 :
            	    // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:940:11: ~ ( '\\n' | '\\r' )
            	    {
            	    if ( (input.LA(1)>='\u0000' && input.LA(1)<='\t')||(input.LA(1)>='\u000B' && input.LA(1)<='\f')||(input.LA(1)>='\u000E' && input.LA(1)<='\uFFFE') ) {
            	        input.consume();

            	    }
            	    else {
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recover(mse);    throw mse;
            	    }


            	    }
            	    break;

            	default :
            	    break loop14;
                }
            } while (true);

             channel=HIDDEN; 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end COMMENT

    public void mTokens() throws RecognitionException {
        // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:1:8: ( KW_TRUE | KW_FALSE | KW_ALL | KW_AND | KW_OR | KW_NOT | KW_LIKE | KW_ASC | KW_DESC | KW_ORDER | KW_BY | KW_GROUP | KW_WHERE | KW_FROM | KW_AS | KW_SELECT | KW_DISTINCT | KW_INSERT | KW_OVERWRITE | KW_OUTER | KW_JOIN | KW_LEFT | KW_RIGHT | KW_FULL | KW_ON | KW_PARTITION | KW_PARTITIONS | KW_TABLE | KW_TABLES | KW_SHOW | KW_DIRECTORY | KW_LOCAL | KW_TRANSFORM | KW_USING | KW_CLUSTER | KW_UNION | KW_LOAD | KW_DATA | KW_INPATH | KW_IS | KW_NULL | KW_CREATE | KW_EXTERNAL | KW_ALTER | KW_DESCRIBE | KW_DROP | KW_RENAME | KW_TO | KW_COMMENT | KW_BOOLEAN | KW_TINYINT | KW_INT | KW_BIGINT | KW_FLOAT | KW_DOUBLE | KW_DATE | KW_DATETIME | KW_TIMESTAMP | KW_STRING | KW_ARRAY | KW_MAP | KW_PARTITIONED | KW_CLUSTERED | KW_SORTED | KW_INTO | KW_BUCKETS | KW_ROW | KW_FORMAT | KW_DELIMITED | KW_FIELDS | KW_TERMINATED | KW_COLLECTION | KW_ITEMS | KW_KEYS | KW_LINES | KW_STORED | KW_SEQUENCEFILE | KW_LOCATION | KW_TABLESAMPLE | KW_BUCKET | KW_OUT | KW_OF | KW_CAST | KW_ADD | KW_REPLACE | KW_COLUMNS | KW_RLIKE | KW_REGEXP | KW_TEMPORARY | KW_FUNCTION | KW_EXPLAIN | KW_EXTENDED | KW_SERIALIZER | KW_WITH | KW_PROPERTIES | KW_LIMIT | DOT | COLON | COMMA | SEMICOLON | LPAREN | RPAREN | LSQUARE | RSQUARE | EQUAL | NOTEQUAL | LESSTHANOREQUALTO | LESSTHAN | GREATERTHANOREQUALTO | GREATERTHAN | DIVIDE | PLUS | MINUS | STAR | MOD | AMPERSAND | TILDE | BITWISEOR | BITWISEXOR | StringLiteral | CharSetLiteral | Number | Identifier | CharSetName | WS | COMMENT )
        int alt15=126;
        alt15 = dfa15.predict(input);
        switch (alt15) {
            case 1 :
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:1:10: KW_TRUE
                {
                mKW_TRUE(); 

                }
                break;
            case 2 :
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:1:18: KW_FALSE
                {
                mKW_FALSE(); 

                }
                break;
            case 3 :
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:1:27: KW_ALL
                {
                mKW_ALL(); 

                }
                break;
            case 4 :
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:1:34: KW_AND
                {
                mKW_AND(); 

                }
                break;
            case 5 :
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:1:41: KW_OR
                {
                mKW_OR(); 

                }
                break;
            case 6 :
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:1:47: KW_NOT
                {
                mKW_NOT(); 

                }
                break;
            case 7 :
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:1:54: KW_LIKE
                {
                mKW_LIKE(); 

                }
                break;
            case 8 :
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:1:62: KW_ASC
                {
                mKW_ASC(); 

                }
                break;
            case 9 :
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:1:69: KW_DESC
                {
                mKW_DESC(); 

                }
                break;
            case 10 :
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:1:77: KW_ORDER
                {
                mKW_ORDER(); 

                }
                break;
            case 11 :
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:1:86: KW_BY
                {
                mKW_BY(); 

                }
                break;
            case 12 :
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:1:92: KW_GROUP
                {
                mKW_GROUP(); 

                }
                break;
            case 13 :
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:1:101: KW_WHERE
                {
                mKW_WHERE(); 

                }
                break;
            case 14 :
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:1:110: KW_FROM
                {
                mKW_FROM(); 

                }
                break;
            case 15 :
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:1:118: KW_AS
                {
                mKW_AS(); 

                }
                break;
            case 16 :
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:1:124: KW_SELECT
                {
                mKW_SELECT(); 

                }
                break;
            case 17 :
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:1:134: KW_DISTINCT
                {
                mKW_DISTINCT(); 

                }
                break;
            case 18 :
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:1:146: KW_INSERT
                {
                mKW_INSERT(); 

                }
                break;
            case 19 :
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:1:156: KW_OVERWRITE
                {
                mKW_OVERWRITE(); 

                }
                break;
            case 20 :
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:1:169: KW_OUTER
                {
                mKW_OUTER(); 

                }
                break;
            case 21 :
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:1:178: KW_JOIN
                {
                mKW_JOIN(); 

                }
                break;
            case 22 :
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:1:186: KW_LEFT
                {
                mKW_LEFT(); 

                }
                break;
            case 23 :
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:1:194: KW_RIGHT
                {
                mKW_RIGHT(); 

                }
                break;
            case 24 :
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:1:203: KW_FULL
                {
                mKW_FULL(); 

                }
                break;
            case 25 :
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:1:211: KW_ON
                {
                mKW_ON(); 

                }
                break;
            case 26 :
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:1:217: KW_PARTITION
                {
                mKW_PARTITION(); 

                }
                break;
            case 27 :
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:1:230: KW_PARTITIONS
                {
                mKW_PARTITIONS(); 

                }
                break;
            case 28 :
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:1:244: KW_TABLE
                {
                mKW_TABLE(); 

                }
                break;
            case 29 :
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:1:253: KW_TABLES
                {
                mKW_TABLES(); 

                }
                break;
            case 30 :
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:1:263: KW_SHOW
                {
                mKW_SHOW(); 

                }
                break;
            case 31 :
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:1:271: KW_DIRECTORY
                {
                mKW_DIRECTORY(); 

                }
                break;
            case 32 :
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:1:284: KW_LOCAL
                {
                mKW_LOCAL(); 

                }
                break;
            case 33 :
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:1:293: KW_TRANSFORM
                {
                mKW_TRANSFORM(); 

                }
                break;
            case 34 :
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:1:306: KW_USING
                {
                mKW_USING(); 

                }
                break;
            case 35 :
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:1:315: KW_CLUSTER
                {
                mKW_CLUSTER(); 

                }
                break;
            case 36 :
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:1:326: KW_UNION
                {
                mKW_UNION(); 

                }
                break;
            case 37 :
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:1:335: KW_LOAD
                {
                mKW_LOAD(); 

                }
                break;
            case 38 :
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:1:343: KW_DATA
                {
                mKW_DATA(); 

                }
                break;
            case 39 :
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:1:351: KW_INPATH
                {
                mKW_INPATH(); 

                }
                break;
            case 40 :
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:1:361: KW_IS
                {
                mKW_IS(); 

                }
                break;
            case 41 :
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:1:367: KW_NULL
                {
                mKW_NULL(); 

                }
                break;
            case 42 :
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:1:375: KW_CREATE
                {
                mKW_CREATE(); 

                }
                break;
            case 43 :
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:1:385: KW_EXTERNAL
                {
                mKW_EXTERNAL(); 

                }
                break;
            case 44 :
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:1:397: KW_ALTER
                {
                mKW_ALTER(); 

                }
                break;
            case 45 :
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:1:406: KW_DESCRIBE
                {
                mKW_DESCRIBE(); 

                }
                break;
            case 46 :
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:1:418: KW_DROP
                {
                mKW_DROP(); 

                }
                break;
            case 47 :
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:1:426: KW_RENAME
                {
                mKW_RENAME(); 

                }
                break;
            case 48 :
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:1:436: KW_TO
                {
                mKW_TO(); 

                }
                break;
            case 49 :
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:1:442: KW_COMMENT
                {
                mKW_COMMENT(); 

                }
                break;
            case 50 :
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:1:453: KW_BOOLEAN
                {
                mKW_BOOLEAN(); 

                }
                break;
            case 51 :
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:1:464: KW_TINYINT
                {
                mKW_TINYINT(); 

                }
                break;
            case 52 :
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:1:475: KW_INT
                {
                mKW_INT(); 

                }
                break;
            case 53 :
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:1:482: KW_BIGINT
                {
                mKW_BIGINT(); 

                }
                break;
            case 54 :
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:1:492: KW_FLOAT
                {
                mKW_FLOAT(); 

                }
                break;
            case 55 :
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:1:501: KW_DOUBLE
                {
                mKW_DOUBLE(); 

                }
                break;
            case 56 :
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:1:511: KW_DATE
                {
                mKW_DATE(); 

                }
                break;
            case 57 :
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:1:519: KW_DATETIME
                {
                mKW_DATETIME(); 

                }
                break;
            case 58 :
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:1:531: KW_TIMESTAMP
                {
                mKW_TIMESTAMP(); 

                }
                break;
            case 59 :
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:1:544: KW_STRING
                {
                mKW_STRING(); 

                }
                break;
            case 60 :
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:1:554: KW_ARRAY
                {
                mKW_ARRAY(); 

                }
                break;
            case 61 :
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:1:563: KW_MAP
                {
                mKW_MAP(); 

                }
                break;
            case 62 :
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:1:570: KW_PARTITIONED
                {
                mKW_PARTITIONED(); 

                }
                break;
            case 63 :
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:1:585: KW_CLUSTERED
                {
                mKW_CLUSTERED(); 

                }
                break;
            case 64 :
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:1:598: KW_SORTED
                {
                mKW_SORTED(); 

                }
                break;
            case 65 :
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:1:608: KW_INTO
                {
                mKW_INTO(); 

                }
                break;
            case 66 :
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:1:616: KW_BUCKETS
                {
                mKW_BUCKETS(); 

                }
                break;
            case 67 :
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:1:627: KW_ROW
                {
                mKW_ROW(); 

                }
                break;
            case 68 :
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:1:634: KW_FORMAT
                {
                mKW_FORMAT(); 

                }
                break;
            case 69 :
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:1:644: KW_DELIMITED
                {
                mKW_DELIMITED(); 

                }
                break;
            case 70 :
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:1:657: KW_FIELDS
                {
                mKW_FIELDS(); 

                }
                break;
            case 71 :
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:1:667: KW_TERMINATED
                {
                mKW_TERMINATED(); 

                }
                break;
            case 72 :
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:1:681: KW_COLLECTION
                {
                mKW_COLLECTION(); 

                }
                break;
            case 73 :
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:1:695: KW_ITEMS
                {
                mKW_ITEMS(); 

                }
                break;
            case 74 :
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:1:704: KW_KEYS
                {
                mKW_KEYS(); 

                }
                break;
            case 75 :
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:1:712: KW_LINES
                {
                mKW_LINES(); 

                }
                break;
            case 76 :
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:1:721: KW_STORED
                {
                mKW_STORED(); 

                }
                break;
            case 77 :
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:1:731: KW_SEQUENCEFILE
                {
                mKW_SEQUENCEFILE(); 

                }
                break;
            case 78 :
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:1:747: KW_LOCATION
                {
                mKW_LOCATION(); 

                }
                break;
            case 79 :
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:1:759: KW_TABLESAMPLE
                {
                mKW_TABLESAMPLE(); 

                }
                break;
            case 80 :
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:1:774: KW_BUCKET
                {
                mKW_BUCKET(); 

                }
                break;
            case 81 :
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:1:784: KW_OUT
                {
                mKW_OUT(); 

                }
                break;
            case 82 :
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:1:791: KW_OF
                {
                mKW_OF(); 

                }
                break;
            case 83 :
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:1:797: KW_CAST
                {
                mKW_CAST(); 

                }
                break;
            case 84 :
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:1:805: KW_ADD
                {
                mKW_ADD(); 

                }
                break;
            case 85 :
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:1:812: KW_REPLACE
                {
                mKW_REPLACE(); 

                }
                break;
            case 86 :
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:1:823: KW_COLUMNS
                {
                mKW_COLUMNS(); 

                }
                break;
            case 87 :
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:1:834: KW_RLIKE
                {
                mKW_RLIKE(); 

                }
                break;
            case 88 :
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:1:843: KW_REGEXP
                {
                mKW_REGEXP(); 

                }
                break;
            case 89 :
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:1:853: KW_TEMPORARY
                {
                mKW_TEMPORARY(); 

                }
                break;
            case 90 :
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:1:866: KW_FUNCTION
                {
                mKW_FUNCTION(); 

                }
                break;
            case 91 :
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:1:878: KW_EXPLAIN
                {
                mKW_EXPLAIN(); 

                }
                break;
            case 92 :
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:1:889: KW_EXTENDED
                {
                mKW_EXTENDED(); 

                }
                break;
            case 93 :
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:1:901: KW_SERIALIZER
                {
                mKW_SERIALIZER(); 

                }
                break;
            case 94 :
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:1:915: KW_WITH
                {
                mKW_WITH(); 

                }
                break;
            case 95 :
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:1:923: KW_PROPERTIES
                {
                mKW_PROPERTIES(); 

                }
                break;
            case 96 :
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:1:937: KW_LIMIT
                {
                mKW_LIMIT(); 

                }
                break;
            case 97 :
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:1:946: DOT
                {
                mDOT(); 

                }
                break;
            case 98 :
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:1:950: COLON
                {
                mCOLON(); 

                }
                break;
            case 99 :
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:1:956: COMMA
                {
                mCOMMA(); 

                }
                break;
            case 100 :
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:1:962: SEMICOLON
                {
                mSEMICOLON(); 

                }
                break;
            case 101 :
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:1:972: LPAREN
                {
                mLPAREN(); 

                }
                break;
            case 102 :
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:1:979: RPAREN
                {
                mRPAREN(); 

                }
                break;
            case 103 :
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:1:986: LSQUARE
                {
                mLSQUARE(); 

                }
                break;
            case 104 :
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:1:994: RSQUARE
                {
                mRSQUARE(); 

                }
                break;
            case 105 :
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:1:1002: EQUAL
                {
                mEQUAL(); 

                }
                break;
            case 106 :
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:1:1008: NOTEQUAL
                {
                mNOTEQUAL(); 

                }
                break;
            case 107 :
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:1:1017: LESSTHANOREQUALTO
                {
                mLESSTHANOREQUALTO(); 

                }
                break;
            case 108 :
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:1:1035: LESSTHAN
                {
                mLESSTHAN(); 

                }
                break;
            case 109 :
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:1:1044: GREATERTHANOREQUALTO
                {
                mGREATERTHANOREQUALTO(); 

                }
                break;
            case 110 :
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:1:1065: GREATERTHAN
                {
                mGREATERTHAN(); 

                }
                break;
            case 111 :
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:1:1077: DIVIDE
                {
                mDIVIDE(); 

                }
                break;
            case 112 :
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:1:1084: PLUS
                {
                mPLUS(); 

                }
                break;
            case 113 :
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:1:1089: MINUS
                {
                mMINUS(); 

                }
                break;
            case 114 :
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:1:1095: STAR
                {
                mSTAR(); 

                }
                break;
            case 115 :
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:1:1100: MOD
                {
                mMOD(); 

                }
                break;
            case 116 :
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:1:1104: AMPERSAND
                {
                mAMPERSAND(); 

                }
                break;
            case 117 :
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:1:1114: TILDE
                {
                mTILDE(); 

                }
                break;
            case 118 :
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:1:1120: BITWISEOR
                {
                mBITWISEOR(); 

                }
                break;
            case 119 :
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:1:1130: BITWISEXOR
                {
                mBITWISEXOR(); 

                }
                break;
            case 120 :
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:1:1141: StringLiteral
                {
                mStringLiteral(); 

                }
                break;
            case 121 :
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:1:1155: CharSetLiteral
                {
                mCharSetLiteral(); 

                }
                break;
            case 122 :
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:1:1170: Number
                {
                mNumber(); 

                }
                break;
            case 123 :
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:1:1177: Identifier
                {
                mIdentifier(); 

                }
                break;
            case 124 :
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:1:1188: CharSetName
                {
                mCharSetName(); 

                }
                break;
            case 125 :
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:1:1200: WS
                {
                mWS(); 

                }
                break;
            case 126 :
                // /root/opencloud/hadoop-0.19.0/src/contrib/hive/ql/src/java/org/apache/hadoop/hive/ql/parse/Hive.g:1:1203: COMMENT
                {
                mCOMMENT(); 

                }
                break;

        }

    }


    protected DFA15 dfa15 = new DFA15(this);
    static final String DFA15_eotS =
        "\1\uffff\24\54\11\uffff\1\155\1\157\2\uffff\1\161\7\uffff\2\165"+
        "\3\uffff\4\54\1\177\12\54\1\u008d\1\54\1\u0090\1\u0091\1\54\1\u0093"+
        "\13\54\1\u00a4\13\54\1\u00b5\17\54\10\uffff\1\u00ca\1\54\1\uffff"+
        "\1\54\1\165\7\54\1\uffff\10\54\1\u00dc\1\u00dd\1\54\1\u00df\1\u00e0"+
        "\1\uffff\1\u00e2\1\54\2\uffff\1\54\1\uffff\1\u00e5\17\54\1\uffff"+
        "\16\54\1\u0106\1\54\1\uffff\1\54\1\u0109\17\54\1\u011a\1\54\2\uffff"+
        "\1\u011e\1\165\3\54\1\u0122\7\54\1\u012a\1\u012b\2\54\2\uffff\1"+
        "\54\2\uffff\1\54\1\uffff\2\54\1\uffff\1\u0132\2\54\1\u0135\1\54"+
        "\1\u0138\1\u0139\1\54\1\u013c\2\54\1\u013f\1\u0140\1\u0142\5\54"+
        "\1\u0148\10\54\1\u0151\2\54\1\u0154\1\uffff\1\54\1\u0156\1\uffff"+
        "\14\54\1\u0163\3\54\1\uffff\1\u0168\1\uffff\1\u00ca\1\uffff\2\54"+
        "\1\u016c\1\uffff\5\54\1\u0172\1\54\2\uffff\1\u0174\1\u0175\1\u0176"+
        "\1\u0177\1\u0178\1\54\1\uffff\1\u017a\1\u017b\1\uffff\1\54\1\u017d"+
        "\2\uffff\2\54\1\uffff\2\54\2\uffff\1\54\1\uffff\4\54\1\u0187\1\uffff"+
        "\1\u0188\7\54\1\uffff\1\u0190\1\54\1\uffff\1\54\1\uffff\3\54\1\u0196"+
        "\1\u0197\1\54\1\u0199\1\u019a\4\54\1\uffff\4\54\1\uffff\2\54\1\u01a6"+
        "\1\uffff\3\54\1\u01aa\1\u01ab\1\uffff\1\54\5\uffff\1\54\2\uffff"+
        "\1\54\1\uffff\5\54\1\u01b4\1\u01b6\1\u01b7\1\54\2\uffff\3\54\1\u01bc"+
        "\1\u01bd\1\u01be\1\u01bf\1\uffff\1\u01c0\1\u01c1\1\54\1\u01c3\1"+
        "\u01c4\2\uffff\1\54\2\uffff\4\54\1\u01ca\6\54\1\uffff\1\54\1\u01d2"+
        "\1\54\2\uffff\10\54\1\uffff\1\u01dc\2\uffff\1\u01dd\3\54\6\uffff"+
        "\1\u01e1\2\uffff\2\54\1\u01e4\1\u01e5\1\u01e7\1\uffff\2\54\1\u01ea"+
        "\4\54\1\uffff\1\54\1\u01f0\1\54\1\u01f2\1\54\1\u01f4\1\u01f5\1\54"+
        "\1\u01f7\2\uffff\3\54\1\uffff\2\54\2\uffff\1\54\1\uffff\1\u01fe"+
        "\1\u01ff\1\uffff\1\54\1\u0201\1\54\1\u0203\1\u0204\1\uffff\1\u0205"+
        "\1\uffff\1\u0206\2\uffff\1\u0207\1\uffff\3\54\1\u020d\1\54\1\u020f"+
        "\2\uffff\1\u0210\1\uffff\1\54\5\uffff\1\54\1\u0213\1\54\1\u0215"+
        "\1\54\1\uffff\1\u0217\2\uffff\1\u0218\1\54\1\uffff\1\54\1\uffff"+
        "\1\u021b\2\uffff\1\54\1\u021d\1\uffff\1\54\1\uffff\1\54\1\u0220"+
        "\1\uffff";
    static final String DFA15_eofS =
        "\u0221\uffff";
    static final String DFA15_minS =
        "\1\11\2\101\1\104\1\106\1\117\1\105\1\101\1\111\1\122\1\110\1\105"+
        "\1\116\1\117\1\105\1\101\1\116\1\101\1\130\1\101\1\105\11\uffff"+
        "\2\75\2\uffff\1\55\6\uffff\1\0\2\60\3\uffff\1\115\1\102\1\101\1"+
        "\115\1\60\1\105\1\122\2\114\2\117\1\122\1\104\1\114\1\104\1\60\1"+
        "\124\2\60\1\105\1\60\1\124\1\114\1\113\1\101\1\106\1\114\1\122\1"+
        "\117\1\124\1\125\1\103\1\60\1\107\2\117\1\124\1\105\1\114\1\117"+
        "\1\122\1\117\1\105\1\120\1\60\1\111\1\127\1\107\1\111\1\107\1\122"+
        "\2\111\1\114\1\125\1\123\1\105\2\120\1\131\7\uffff\1\0\1\47\1\60"+
        "\1\uffff\1\53\1\60\1\115\1\120\1\114\1\105\1\116\1\131\1\105\1\uffff"+
        "\1\114\1\115\1\123\1\103\1\114\1\115\2\101\2\60\1\105\2\60\1\uffff"+
        "\1\60\1\105\2\uffff\1\122\1\uffff\1\60\1\114\1\111\2\105\1\101\1"+
        "\104\1\124\1\111\1\103\1\124\1\105\1\120\1\101\1\102\1\113\1\uffff"+
        "\1\111\1\114\1\125\1\110\1\122\1\104\1\125\1\105\1\122\1\111\1\124"+
        "\1\127\1\115\1\101\1\60\1\105\1\uffff\1\116\1\60\1\114\1\101\1\105"+
        "\1\113\1\110\1\124\1\117\1\116\1\114\1\115\1\123\1\124\1\101\1\105"+
        "\1\114\1\60\1\123\1\0\1\uffff\2\60\1\111\1\117\1\105\1\60\1\123"+
        "\1\111\1\123\1\104\1\101\1\105\1\124\2\60\1\124\1\131\2\uffff\1"+
        "\122\2\uffff\1\122\1\uffff\1\122\1\127\1\uffff\1\60\1\124\1\123"+
        "\1\60\1\114\2\60\1\115\1\60\1\111\1\103\3\60\1\114\1\105\1\116\1"+
        "\105\1\120\1\60\2\105\1\101\1\105\1\103\1\105\1\116\1\105\1\60\1"+
        "\123\1\124\1\60\1\uffff\1\122\1\60\1\uffff\1\101\1\115\1\130\1\105"+
        "\1\124\1\111\1\116\1\107\1\105\1\115\1\105\1\124\1\60\1\124\1\116"+
        "\1\101\1\uffff\1\60\1\0\1\47\1\uffff\1\116\1\122\1\60\1\uffff\1"+
        "\106\1\116\1\124\1\123\1\124\1\60\1\111\2\uffff\5\60\1\122\1\uffff"+
        "\2\60\1\uffff\1\111\1\60\2\uffff\2\111\1\uffff\1\116\1\124\2\uffff"+
        "\1\111\1\uffff\1\105\2\124\1\101\1\60\1\uffff\1\60\1\120\1\114\1"+
        "\116\1\124\1\104\1\107\1\104\1\uffff\1\60\1\110\1\uffff\1\124\1"+
        "\uffff\1\103\1\105\1\120\2\60\1\124\2\60\1\103\2\116\1\105\1\uffff"+
        "\1\105\1\116\1\104\1\111\1\uffff\2\101\1\60\1\uffff\1\117\1\124"+
        "\1\101\2\60\1\uffff\1\117\5\uffff\1\111\2\uffff\1\117\1\uffff\1"+
        "\124\1\102\1\103\1\117\1\115\3\60\1\116\2\uffff\1\122\1\111\1\103"+
        "\4\60\1\uffff\2\60\1\105\2\60\2\uffff\1\111\2\uffff\1\124\1\123"+
        "\1\124\1\122\1\60\1\101\1\105\1\116\1\124\1\122\1\115\1\uffff\1"+
        "\122\1\60\1\115\2\uffff\1\116\1\124\1\116\2\105\1\124\1\122\1\105"+
        "\1\uffff\1\60\2\uffff\1\60\1\117\1\132\1\105\6\uffff\1\60\2\uffff"+
        "\1\117\1\111\3\60\1\uffff\1\114\1\104\1\60\1\105\1\131\1\120\1\115"+
        "\1\uffff\1\120\1\60\1\105\1\60\1\104\2\60\1\131\1\60\2\uffff\1\120"+
        "\1\105\1\106\1\uffff\1\116\1\117\2\uffff\1\104\1\uffff\2\60\1\uffff"+
        "\1\104\1\60\1\114\2\60\1\uffff\1\60\1\uffff\1\60\2\uffff\1\60\1"+
        "\uffff\1\105\1\122\1\111\1\60\1\116\1\60\2\uffff\1\60\1\uffff\1"+
        "\105\5\uffff\1\122\1\60\1\114\1\60\1\104\1\uffff\1\60\2\uffff\1"+
        "\60\1\124\1\uffff\1\105\1\uffff\1\60\2\uffff\1\111\1\60\1\uffff"+
        "\1\105\1\uffff\1\123\1\60\1\uffff";
    static final String DFA15_maxS =
        "\1\176\1\122\1\125\1\123\1\126\1\125\1\117\1\122\1\131\1\122\1\111"+
        "\2\124\2\117\1\101\1\123\1\122\1\130\1\101\1\105\11\uffff\1\76\1"+
        "\75\2\uffff\1\55\6\uffff\1\ufffe\2\172\3\uffff\1\122\1\102\1\125"+
        "\1\116\1\172\1\105\1\122\1\114\1\116\2\117\1\122\1\104\1\124\1\104"+
        "\1\172\1\124\2\172\1\105\1\172\1\124\1\114\1\116\1\103\1\106\2\123"+
        "\1\117\1\124\1\125\1\103\1\172\1\107\2\117\1\124\1\105\3\122\1\117"+
        "\1\105\1\124\1\172\1\111\1\127\1\120\1\111\1\107\1\122\2\111\1\115"+
        "\1\125\1\123\1\105\1\124\1\120\1\131\7\uffff\1\ufffe\1\47\1\146"+
        "\1\uffff\1\71\1\172\1\115\1\120\1\114\1\105\1\116\1\131\1\105\1"+
        "\uffff\1\114\1\115\1\123\1\103\1\114\1\115\2\101\2\172\1\105\2\172"+
        "\1\uffff\1\172\1\105\2\uffff\1\122\1\uffff\1\172\1\114\1\111\2\105"+
        "\1\101\1\104\1\124\1\111\1\103\1\124\1\105\1\120\1\105\1\102\1\113"+
        "\1\uffff\1\111\1\114\1\125\1\110\1\122\1\111\1\125\1\105\1\122\1"+
        "\111\1\124\1\127\1\115\1\101\1\172\1\105\1\uffff\1\116\1\172\1\114"+
        "\1\101\1\105\1\113\1\110\1\124\1\117\1\116\1\125\1\115\1\123\1\124"+
        "\1\101\1\105\1\114\1\172\1\123\1\ufffe\1\uffff\2\172\1\111\1\117"+
        "\1\105\1\172\1\123\1\111\1\123\1\104\1\101\1\105\1\124\2\172\1\124"+
        "\1\131\2\uffff\1\122\2\uffff\1\122\1\uffff\1\122\1\127\1\uffff\1"+
        "\172\1\124\1\123\1\172\1\124\2\172\1\115\1\172\1\111\1\103\3\172"+
        "\1\114\1\105\1\116\1\105\1\120\1\172\2\105\1\101\1\105\1\103\1\105"+
        "\1\116\1\105\1\172\1\123\1\124\1\172\1\uffff\1\122\1\172\1\uffff"+
        "\1\101\1\115\1\130\1\105\1\124\1\111\1\116\1\107\1\105\1\115\1\105"+
        "\1\124\1\172\1\124\1\122\1\101\1\uffff\1\172\1\ufffe\1\47\1\uffff"+
        "\1\116\1\122\1\172\1\uffff\1\106\1\116\1\124\1\123\1\124\1\172\1"+
        "\111\2\uffff\5\172\1\122\1\uffff\2\172\1\uffff\1\111\1\172\2\uffff"+
        "\2\111\1\uffff\1\116\1\124\2\uffff\1\111\1\uffff\1\105\2\124\1\101"+
        "\1\172\1\uffff\1\172\1\120\1\114\1\116\1\124\1\104\1\107\1\104\1"+
        "\uffff\1\172\1\110\1\uffff\1\124\1\uffff\1\103\1\105\1\120\2\172"+
        "\1\124\2\172\1\103\2\116\1\105\1\uffff\1\105\1\116\1\104\1\111\1"+
        "\uffff\2\101\1\172\1\uffff\1\117\1\124\1\101\2\172\1\uffff\1\117"+
        "\5\uffff\1\111\2\uffff\1\117\1\uffff\1\124\1\102\1\103\1\117\1\115"+
        "\3\172\1\116\2\uffff\1\122\1\111\1\103\4\172\1\uffff\2\172\1\105"+
        "\2\172\2\uffff\1\111\2\uffff\1\124\1\123\1\124\1\122\1\172\1\101"+
        "\1\105\1\116\1\124\1\122\1\115\1\uffff\1\122\1\172\1\115\2\uffff"+
        "\1\116\1\124\1\116\2\105\1\124\1\122\1\105\1\uffff\1\172\2\uffff"+
        "\1\172\1\117\1\132\1\105\6\uffff\1\172\2\uffff\1\117\1\111\3\172"+
        "\1\uffff\1\114\1\104\1\172\1\105\1\131\1\120\1\115\1\uffff\1\120"+
        "\1\172\1\105\1\172\1\104\2\172\1\131\1\172\2\uffff\1\120\1\105\1"+
        "\106\1\uffff\1\116\1\117\2\uffff\1\104\1\uffff\2\172\1\uffff\1\104"+
        "\1\172\1\114\2\172\1\uffff\1\172\1\uffff\1\172\2\uffff\1\172\1\uffff"+
        "\1\105\1\122\1\111\1\172\1\116\1\172\2\uffff\1\172\1\uffff\1\105"+
        "\5\uffff\1\122\1\172\1\114\1\172\1\104\1\uffff\1\172\2\uffff\1\172"+
        "\1\124\1\uffff\1\105\1\uffff\1\172\2\uffff\1\111\1\172\1\uffff\1"+
        "\105\1\uffff\1\123\1\172\1\uffff";
    static final String DFA15_acceptS =
        "\25\uffff\1\141\1\142\1\143\1\144\1\145\1\146\1\147\1\150\1\151"+
        "\2\uffff\1\157\1\160\1\uffff\1\162\1\163\1\164\1\165\1\166\1\167"+
        "\3\uffff\1\173\1\174\1\175\74\uffff\1\152\1\153\1\154\1\155\1\156"+
        "\1\176\1\161\3\uffff\1\172\11\uffff\1\60\15\uffff\1\17\2\uffff\1"+
        "\5\1\122\1\uffff\1\31\20\uffff\1\13\20\uffff\1\50\24\uffff\1\170"+
        "\21\uffff\1\4\1\3\1\uffff\1\124\1\10\1\uffff\1\121\2\uffff\1\6\40"+
        "\uffff\1\64\2\uffff\1\103\20\uffff\1\75\3\uffff\1\171\3\uffff\1"+
        "\1\7\uffff\1\30\1\16\6\uffff\1\51\2\uffff\1\7\2\uffff\1\45\1\26"+
        "\2\uffff\1\11\2\uffff\1\56\1\46\1\uffff\1\70\5\uffff\1\136\10\uffff"+
        "\1\36\2\uffff\1\101\1\uffff\1\25\14\uffff\1\123\4\uffff\1\112\3"+
        "\uffff\1\34\5\uffff\1\2\1\uffff\1\66\1\74\1\54\1\24\1\12\1\uffff"+
        "\1\140\1\113\1\uffff\1\40\11\uffff\1\14\1\15\7\uffff\1\111\5\uffff"+
        "\1\127\1\27\1\uffff\1\44\1\42\13\uffff\1\35\3\uffff\1\106\1\104"+
        "\10\uffff\1\67\1\uffff\1\120\1\65\4\uffff\1\20\1\114\1\73\1\100"+
        "\1\47\1\22\1\uffff\1\57\1\130\5\uffff\1\52\7\uffff\1\63\11\uffff"+
        "\1\102\1\62\3\uffff\1\125\2\uffff\1\126\1\61\1\uffff\1\43\2\uffff"+
        "\1\133\5\uffff\1\132\1\uffff\1\116\1\uffff\1\55\1\21\1\uffff\1\71"+
        "\6\uffff\1\53\1\134\1\uffff\1\131\1\uffff\1\41\1\72\1\23\1\105\1"+
        "\37\5\uffff\1\32\1\uffff\1\77\1\107\2\uffff\1\135\1\uffff\1\33\1"+
        "\uffff\1\110\1\117\2\uffff\1\76\1\uffff\1\115\2\uffff\1\137";
    static final String DFA15_specialS =
        "\u0221\uffff}>";
    static final String[] DFA15_transitionS = {
            "\2\56\2\uffff\1\56\22\uffff\1\56\4\uffff\1\44\1\45\1\51\1\31"+
            "\1\32\1\43\1\41\1\27\1\42\1\25\1\40\1\52\11\53\1\26\1\30\1\36"+
            "\1\35\1\37\2\uffff\1\3\1\10\1\21\1\7\1\22\1\2\1\11\1\54\1\14"+
            "\1\15\1\24\1\6\1\23\1\5\1\4\1\17\1\54\1\16\1\13\1\1\1\20\1\54"+
            "\1\12\3\54\1\33\1\uffff\1\34\1\50\1\55\1\uffff\32\54\1\uffff"+
            "\1\47\1\uffff\1\46",
            "\1\60\3\uffff\1\57\3\uffff\1\62\5\uffff\1\63\2\uffff\1\61",
            "\1\66\7\uffff\1\64\2\uffff\1\71\2\uffff\1\65\2\uffff\1\70\2"+
            "\uffff\1\67",
            "\1\75\7\uffff\1\74\1\uffff\1\73\3\uffff\1\72\1\76",
            "\1\101\7\uffff\1\103\3\uffff\1\100\2\uffff\1\77\1\102",
            "\1\104\5\uffff\1\105",
            "\1\110\3\uffff\1\106\5\uffff\1\107",
            "\1\114\3\uffff\1\111\3\uffff\1\112\5\uffff\1\115\2\uffff\1\113",
            "\1\120\5\uffff\1\121\5\uffff\1\116\3\uffff\1\117",
            "\1\122",
            "\1\124\1\123",
            "\1\125\2\uffff\1\130\6\uffff\1\127\4\uffff\1\126",
            "\1\132\4\uffff\1\133\1\131",
            "\1\134",
            "\1\136\3\uffff\1\140\2\uffff\1\137\2\uffff\1\135",
            "\1\141",
            "\1\142\4\uffff\1\143",
            "\1\146\12\uffff\1\145\2\uffff\1\144\2\uffff\1\147",
            "\1\150",
            "\1\151",
            "\1\152",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\154\1\153",
            "\1\156",
            "",
            "",
            "\1\160",
            "",
            "",
            "",
            "",
            "",
            "",
            "\47\162\1\163\uffd7\162",
            "\12\167\7\uffff\27\54\1\164\2\54\4\uffff\1\54\1\uffff\4\54\1"+
            "\166\25\54",
            "\12\167\7\uffff\32\54\4\uffff\1\54\1\uffff\4\54\1\166\25\54",
            "",
            "",
            "",
            "\1\171\4\uffff\1\170",
            "\1\172",
            "\1\174\23\uffff\1\173",
            "\1\176\1\175",
            "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54",
            "\1\u0080",
            "\1\u0081",
            "\1\u0082",
            "\1\u0084\1\uffff\1\u0083",
            "\1\u0085",
            "\1\u0086",
            "\1\u0087",
            "\1\u0088",
            "\1\u0089\7\uffff\1\u008a",
            "\1\u008b",
            "\12\54\7\uffff\2\54\1\u008c\27\54\4\uffff\1\54\1\uffff\32\54",
            "\1\u008e",
            "\12\54\7\uffff\3\54\1\u008f\26\54\4\uffff\1\54\1\uffff\32\54",
            "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54",
            "\1\u0092",
            "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54",
            "\1\u0094",
            "\1\u0095",
            "\1\u0098\1\uffff\1\u0096\1\u0097",
            "\1\u009a\1\uffff\1\u0099",
            "\1\u009b",
            "\1\u009c\6\uffff\1\u009d",
            "\1\u009f\1\u009e",
            "\1\u00a0",
            "\1\u00a1",
            "\1\u00a2",
            "\1\u00a3",
            "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54",
            "\1\u00a5",
            "\1\u00a6",
            "\1\u00a7",
            "\1\u00a8",
            "\1\u00a9",
            "\1\u00ac\4\uffff\1\u00ab\1\u00aa",
            "\1\u00ad\2\uffff\1\u00ae",
            "\1\u00af",
            "\1\u00b0",
            "\1\u00b1",
            "\1\u00b2\2\uffff\1\u00b4\1\u00b3",
            "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54",
            "\1\u00b6",
            "\1\u00b7",
            "\1\u00ba\6\uffff\1\u00b9\1\uffff\1\u00b8",
            "\1\u00bb",
            "\1\u00bc",
            "\1\u00bd",
            "\1\u00be",
            "\1\u00bf",
            "\1\u00c0\1\u00c1",
            "\1\u00c2",
            "\1\u00c3",
            "\1\u00c4",
            "\1\u00c6\3\uffff\1\u00c5",
            "\1\u00c7",
            "\1\u00c8",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\47\162\1\163\uffd7\162",
            "\1\u00c9",
            "\12\u00cb\7\uffff\6\u00cb\32\uffff\6\u00cb",
            "",
            "\1\165\1\uffff\1\165\2\uffff\12\u00cc",
            "\12\167\7\uffff\32\54\4\uffff\1\54\1\uffff\4\54\1\166\25\54",
            "\1\u00cd",
            "\1\u00ce",
            "\1\u00cf",
            "\1\u00d0",
            "\1\u00d1",
            "\1\u00d2",
            "\1\u00d3",
            "",
            "\1\u00d4",
            "\1\u00d5",
            "\1\u00d6",
            "\1\u00d7",
            "\1\u00d8",
            "\1\u00d9",
            "\1\u00da",
            "\1\u00db",
            "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54",
            "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54",
            "\1\u00de",
            "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54",
            "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54",
            "",
            "\12\54\7\uffff\4\54\1\u00e1\25\54\4\uffff\1\54\1\uffff\32\54",
            "\1\u00e3",
            "",
            "",
            "\1\u00e4",
            "",
            "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54",
            "\1\u00e6",
            "\1\u00e7",
            "\1\u00e8",
            "\1\u00e9",
            "\1\u00ea",
            "\1\u00eb",
            "\1\u00ec",
            "\1\u00ed",
            "\1\u00ee",
            "\1\u00ef",
            "\1\u00f0",
            "\1\u00f1",
            "\1\u00f2\3\uffff\1\u00f3",
            "\1\u00f4",
            "\1\u00f5",
            "",
            "\1\u00f6",
            "\1\u00f7",
            "\1\u00f8",
            "\1\u00f9",
            "\1\u00fa",
            "\1\u00fb\4\uffff\1\u00fc",
            "\1\u00fd",
            "\1\u00fe",
            "\1\u00ff",
            "\1\u0100",
            "\1\u0101",
            "\1\u0102",
            "\1\u0103",
            "\1\u0104",
            "\12\54\7\uffff\16\54\1\u0105\13\54\4\uffff\1\54\1\uffff\32\54",
            "\1\u0107",
            "",
            "\1\u0108",
            "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54",
            "\1\u010a",
            "\1\u010b",
            "\1\u010c",
            "\1\u010d",
            "\1\u010e",
            "\1\u010f",
            "\1\u0110",
            "\1\u0111",
            "\1\u0112\10\uffff\1\u0113",
            "\1\u0114",
            "\1\u0115",
            "\1\u0116",
            "\1\u0117",
            "\1\u0118",
            "\1\u0119",
            "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54",
            "\1\u011b",
            "\47\u011c\1\u011d\uffd7\u011c",
            "",
            "\12\u00cb\7\uffff\6\u00cb\24\54\4\uffff\1\54\1\uffff\6\u00cb"+
            "\24\54",
            "\12\u00cc\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54",
            "\1\u011f",
            "\1\u0120",
            "\1\u0121",
            "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54",
            "\1\u0123",
            "\1\u0124",
            "\1\u0125",
            "\1\u0126",
            "\1\u0127",
            "\1\u0128",
            "\1\u0129",
            "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54",
            "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54",
            "\1\u012c",
            "\1\u012d",
            "",
            "",
            "\1\u012e",
            "",
            "",
            "\1\u012f",
            "",
            "\1\u0130",
            "\1\u0131",
            "",
            "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54",
            "\1\u0133",
            "\1\u0134",
            "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54",
            "\1\u0137\7\uffff\1\u0136",
            "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54",
            "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54",
            "\1\u013a",
            "\12\54\7\uffff\21\54\1\u013b\10\54\4\uffff\1\54\1\uffff\32\54",
            "\1\u013d",
            "\1\u013e",
            "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54",
            "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54",
            "\12\54\7\uffff\23\54\1\u0141\6\54\4\uffff\1\54\1\uffff\32\54",
            "\1\u0143",
            "\1\u0144",
            "\1\u0145",
            "\1\u0146",
            "\1\u0147",
            "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54",
            "\1\u0149",
            "\1\u014a",
            "\1\u014b",
            "\1\u014c",
            "\1\u014d",
            "\1\u014e",
            "\1\u014f",
            "\1\u0150",
            "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54",
            "\1\u0152",
            "\1\u0153",
            "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54",
            "",
            "\1\u0155",
            "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54",
            "",
            "\1\u0157",
            "\1\u0158",
            "\1\u0159",
            "\1\u015a",
            "\1\u015b",
            "\1\u015c",
            "\1\u015d",
            "\1\u015e",
            "\1\u015f",
            "\1\u0160",
            "\1\u0161",
            "\1\u0162",
            "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54",
            "\1\u0164",
            "\1\u0166\3\uffff\1\u0165",
            "\1\u0167",
            "",
            "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54",
            "\47\u011c\1\u011d\uffd7\u011c",
            "\1\u00c9",
            "",
            "\1\u0169",
            "\1\u016a",
            "\12\54\7\uffff\22\54\1\u016b\7\54\4\uffff\1\54\1\uffff\32\54",
            "",
            "\1\u016d",
            "\1\u016e",
            "\1\u016f",
            "\1\u0170",
            "\1\u0171",
            "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54",
            "\1\u0173",
            "",
            "",
            "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54",
            "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54",
            "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54",
            "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54",
            "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54",
            "\1\u0179",
            "",
            "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54",
            "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54",
            "",
            "\1\u017c",
            "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54",
            "",
            "",
            "\1\u017e",
            "\1\u017f",
            "",
            "\1\u0180",
            "\1\u0181",
            "",
            "",
            "\1\u0182",
            "",
            "\1\u0183",
            "\1\u0184",
            "\1\u0185",
            "\1\u0186",
            "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54",
            "",
            "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54",
            "\1\u0189",
            "\1\u018a",
            "\1\u018b",
            "\1\u018c",
            "\1\u018d",
            "\1\u018e",
            "\1\u018f",
            "",
            "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54",
            "\1\u0191",
            "",
            "\1\u0192",
            "",
            "\1\u0193",
            "\1\u0194",
            "\1\u0195",
            "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54",
            "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54",
            "\1\u0198",
            "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54",
            "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54",
            "\1\u019b",
            "\1\u019c",
            "\1\u019d",
            "\1\u019e",
            "",
            "\1\u019f",
            "\1\u01a0",
            "\1\u01a1",
            "\1\u01a2",
            "",
            "\1\u01a3",
            "\1\u01a4",
            "\12\54\7\uffff\1\u01a5\31\54\4\uffff\1\54\1\uffff\32\54",
            "",
            "\1\u01a7",
            "\1\u01a8",
            "\1\u01a9",
            "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54",
            "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54",
            "",
            "\1\u01ac",
            "",
            "",
            "",
            "",
            "",
            "\1\u01ad",
            "",
            "",
            "\1\u01ae",
            "",
            "\1\u01af",
            "\1\u01b0",
            "\1\u01b1",
            "\1\u01b2",
            "\1\u01b3",
            "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54",
            "\12\54\7\uffff\22\54\1\u01b5\7\54\4\uffff\1\54\1\uffff\32\54",
            "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54",
            "\1\u01b8",
            "",
            "",
            "\1\u01b9",
            "\1\u01ba",
            "\1\u01bb",
            "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54",
            "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54",
            "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54",
            "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54",
            "",
            "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54",
            "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54",
            "\1\u01c2",
            "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54",
            "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54",
            "",
            "",
            "\1\u01c5",
            "",
            "",
            "\1\u01c6",
            "\1\u01c7",
            "\1\u01c8",
            "\1\u01c9",
            "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54",
            "\1\u01cb",
            "\1\u01cc",
            "\1\u01cd",
            "\1\u01ce",
            "\1\u01cf",
            "\1\u01d0",
            "",
            "\1\u01d1",
            "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54",
            "\1\u01d3",
            "",
            "",
            "\1\u01d4",
            "\1\u01d5",
            "\1\u01d6",
            "\1\u01d7",
            "\1\u01d8",
            "\1\u01d9",
            "\1\u01da",
            "\1\u01db",
            "",
            "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54",
            "",
            "",
            "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54",
            "\1\u01de",
            "\1\u01df",
            "\1\u01e0",
            "",
            "",
            "",
            "",
            "",
            "",
            "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54",
            "",
            "",
            "\1\u01e2",
            "\1\u01e3",
            "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54",
            "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54",
            "\12\54\7\uffff\4\54\1\u01e6\25\54\4\uffff\1\54\1\uffff\32\54",
            "",
            "\1\u01e8",
            "\1\u01e9",
            "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54",
            "\1\u01eb",
            "\1\u01ec",
            "\1\u01ed",
            "\1\u01ee",
            "",
            "\1\u01ef",
            "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54",
            "\1\u01f1",
            "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54",
            "\1\u01f3",
            "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54",
            "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54",
            "\1\u01f6",
            "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54",
            "",
            "",
            "\1\u01f8",
            "\1\u01f9",
            "\1\u01fa",
            "",
            "\1\u01fb",
            "\1\u01fc",
            "",
            "",
            "\1\u01fd",
            "",
            "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54",
            "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54",
            "",
            "\1\u0200",
            "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54",
            "\1\u0202",
            "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54",
            "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54",
            "",
            "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54",
            "",
            "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54",
            "",
            "",
            "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54",
            "",
            "\1\u0208",
            "\1\u0209",
            "\1\u020a",
            "\12\54\7\uffff\4\54\1\u020c\15\54\1\u020b\7\54\4\uffff\1\54"+
            "\1\uffff\32\54",
            "\1\u020e",
            "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54",
            "",
            "",
            "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54",
            "",
            "\1\u0211",
            "",
            "",
            "",
            "",
            "",
            "\1\u0212",
            "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54",
            "\1\u0214",
            "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54",
            "\1\u0216",
            "",
            "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54",
            "",
            "",
            "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54",
            "\1\u0219",
            "",
            "\1\u021a",
            "",
            "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54",
            "",
            "",
            "\1\u021c",
            "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54",
            "",
            "\1\u021e",
            "",
            "\1\u021f",
            "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54",
            ""
    };

    static final short[] DFA15_eot = DFA.unpackEncodedString(DFA15_eotS);
    static final short[] DFA15_eof = DFA.unpackEncodedString(DFA15_eofS);
    static final char[] DFA15_min = DFA.unpackEncodedStringToUnsignedChars(DFA15_minS);
    static final char[] DFA15_max = DFA.unpackEncodedStringToUnsignedChars(DFA15_maxS);
    static final short[] DFA15_accept = DFA.unpackEncodedString(DFA15_acceptS);
    static final short[] DFA15_special = DFA.unpackEncodedString(DFA15_specialS);
    static final short[][] DFA15_transition;

    static {
        int numStates = DFA15_transitionS.length;
        DFA15_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA15_transition[i] = DFA.unpackEncodedString(DFA15_transitionS[i]);
        }
    }

    class DFA15 extends DFA {

        public DFA15(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 15;
            this.eot = DFA15_eot;
            this.eof = DFA15_eof;
            this.min = DFA15_min;
            this.max = DFA15_max;
            this.accept = DFA15_accept;
            this.special = DFA15_special;
            this.transition = DFA15_transition;
        }
        public String getDescription() {
            return "1:1: Tokens : ( KW_TRUE | KW_FALSE | KW_ALL | KW_AND | KW_OR | KW_NOT | KW_LIKE | KW_ASC | KW_DESC | KW_ORDER | KW_BY | KW_GROUP | KW_WHERE | KW_FROM | KW_AS | KW_SELECT | KW_DISTINCT | KW_INSERT | KW_OVERWRITE | KW_OUTER | KW_JOIN | KW_LEFT | KW_RIGHT | KW_FULL | KW_ON | KW_PARTITION | KW_PARTITIONS | KW_TABLE | KW_TABLES | KW_SHOW | KW_DIRECTORY | KW_LOCAL | KW_TRANSFORM | KW_USING | KW_CLUSTER | KW_UNION | KW_LOAD | KW_DATA | KW_INPATH | KW_IS | KW_NULL | KW_CREATE | KW_EXTERNAL | KW_ALTER | KW_DESCRIBE | KW_DROP | KW_RENAME | KW_TO | KW_COMMENT | KW_BOOLEAN | KW_TINYINT | KW_INT | KW_BIGINT | KW_FLOAT | KW_DOUBLE | KW_DATE | KW_DATETIME | KW_TIMESTAMP | KW_STRING | KW_ARRAY | KW_MAP | KW_PARTITIONED | KW_CLUSTERED | KW_SORTED | KW_INTO | KW_BUCKETS | KW_ROW | KW_FORMAT | KW_DELIMITED | KW_FIELDS | KW_TERMINATED | KW_COLLECTION | KW_ITEMS | KW_KEYS | KW_LINES | KW_STORED | KW_SEQUENCEFILE | KW_LOCATION | KW_TABLESAMPLE | KW_BUCKET | KW_OUT | KW_OF | KW_CAST | KW_ADD | KW_REPLACE | KW_COLUMNS | KW_RLIKE | KW_REGEXP | KW_TEMPORARY | KW_FUNCTION | KW_EXPLAIN | KW_EXTENDED | KW_SERIALIZER | KW_WITH | KW_PROPERTIES | KW_LIMIT | DOT | COLON | COMMA | SEMICOLON | LPAREN | RPAREN | LSQUARE | RSQUARE | EQUAL | NOTEQUAL | LESSTHANOREQUALTO | LESSTHAN | GREATERTHANOREQUALTO | GREATERTHAN | DIVIDE | PLUS | MINUS | STAR | MOD | AMPERSAND | TILDE | BITWISEOR | BITWISEXOR | StringLiteral | CharSetLiteral | Number | Identifier | CharSetName | WS | COMMENT );";
        }
    }
 

}