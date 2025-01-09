CREATE OR REPLACE TABLE objects (
	name VARCHAR(50),
	embedding BLOB NOT NULL DEFAULT 0,
	VECTOR INDEX embedding_idx (embedding)
);

INSERT INTO objects (name, embedding)
VALUES
	('Alarm clock', VEC_FromText("[0.001, 0]")),
	('Cow', VEC_FromText("[1.0, 0.05]")),
	('Bicycle', VEC_FromText("[0.2, 0.156]")),
	('Eagle', VEC_FromText("[0.03, 0.9]"));

SELECT name
FROM objects
ORDER BY VEC_Distance(
	embedding,
	VEC_FromText('[0.01, 0.01]') -- small and slow
)
LIMIT 1;

SELECT name
FROM objects
ORDER BY VEC_Distance(
	embedding,
	VEC_FromText('[0.9, 0.5]') -- big and semi-fast
)
LIMIT 1;

SELECT name
FROM objects
ORDER BY VEC_Distance(
	embedding,
	VEC_FromText('[0.2, 0.8]') -- small and fast
)
LIMIT 1;
