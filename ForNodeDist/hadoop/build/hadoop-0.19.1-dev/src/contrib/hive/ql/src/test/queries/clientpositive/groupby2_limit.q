set mapred.reduce.tasks=31;

EXPLAIN
SELECT src.key, sum(substr(src.value,4)) FROM src GROUP BY src.key LIMIT 5;

SELECT src.key, sum(substr(src.value,4)) FROM src GROUP BY src.key LIMIT 5;

