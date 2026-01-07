CREATE TABLE IF NOT EXISTS technologies (
	id int NOT NULL AUTO_INCREMENT,
	name varchar(50) NULL,
	description varchar(90) NULL,
	PRIMARY KEY (`id`)
)
ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE IF NOT EXISTS capacities_x_tecnologies (
	id INT auto_increment NOT NULL,
	id_capacity INT NOT NULL,
	id_tecnology INT NOT NULL,
	CONSTRAINT capacities_x_tecnologies_pk PRIMARY KEY (id),
	CONSTRAINT capacities_x_tecnologies_unique UNIQUE KEY (id_capacity,id_tecnology)
)
ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_0900_ai_ci;
