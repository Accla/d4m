namespace java cloudbase.core.data
namespace rb Cloudbase.Proxy

typedef i64 ScanID
typedef i64 UpdateID

struct Key {
	1:i32 colFamilyOffset;
	2:i32 colQualifierOffset;
	3:i32 colVisibilityOffset;
	4:i32 totalLen;
	5:binary keyData,
	6:i64 timestamp
}

struct Column {
	1:binary columnFamily,
	2:binary columnQualifier,
	3:binary columnVisibility
}

struct ColumnUpdate {
	1:Column column,
	2:i64 timestamp,
	3:bool hasTimestamp,
	4:binary value,
	5:bool deleted
}

struct Mutation {
	1:binary row,
	2:list<ColumnUpdate> updates 
}

struct KeyExtent {
	1:binary table,
	2:binary endRow,
	3:binary prevEndRow
}

struct KeyValue {
	1:Key key,
	2:binary value
}

struct ScanResult {
	1:list<KeyValue> data,
	2:bool more
}

struct Range {
	1:Key start,
	2:Key stop,
	3:bool startKeyInclusive,
	4:bool stopKeyInclusive,
	5:bool infiniteStartKey,
	6:bool infiniteStopKey
}

struct InitialScan {
	1:ScanID scanID,
	2:ScanResult result
}

struct IterInfo {
	1:i32 priority,
	2:string className,
	3:string iterName
}

struct ConstraintViolationSummary {
	1:string constrainClass,
	2:i16 violationCode,
	3:string violationDescription,
	4:i64 numberOfViolatingMutations
}

struct UpdateErrors {
	1:map<KeyExtent, i64> failedExtents,
	2:list<ConstraintViolationSummary> violationSummaries,
	3:list<KeyExtent> authorizationFailures
}

typedef map<KeyExtent,list<Mutation>> UpdateBatch

typedef map<KeyExtent, list<Range>> ScanBatch

typedef map<KeyExtent, map<string, i64>> TabletFiles
