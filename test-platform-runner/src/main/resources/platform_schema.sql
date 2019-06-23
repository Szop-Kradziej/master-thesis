CREATE TABLE projects
(
    id   SERIAL,
    name VARCHAR(128) NOT NULL,

    CONSTRAINT projects_pk PRIMARY KEY (id),
    CONSTRAINT projects_name_uk UNIQUE (name)
);

CREATE TABLE groups
(
    id         SERIAL,
    name       VARCHAR(128) NOT NULL,
    project_id BIGINT       NOT NULL,

    CONSTRAINT groups_pk PRIMARY KEY (id),
    CONSTRAINT groups_projects_fk FOREIGN KEY (project_id) REFERENCES projects (id),
    CONSTRAINT groups_name_project_uk UNIQUE (name, project_id)
);

CREATE TABLE users
(
    id         SERIAL,
    name       VARCHAR(128) NOT NULL,
    password   VARCHAR(64)  NOT NULL DEFAULT 'password',
    is_student BOOLEAN      NOT NULL DEFAULT TRUE,

    CONSTRAINT users_pk PRIMARY KEY (id),
    CONSTRAINT users_name_uk UNIQUE (name)
);

CREATE TABLE students_in_groups
(
    id         SERIAL,
    group_id   BIGINT NOT NULL,
    student_id BIGINT NOT NULL,

    CONSTRAINT students_in_groups_pk PRIMARY KEY (id),
    CONSTRAINT students_in_groups_groups_fk FOREIGN KEY (group_id) REFERENCES groups (id),
    CONSTRAINT students_in_groups_users_fk FOREIGN KEY (student_id) REFERENCES users (id),
    CONSTRAINT students_in_groups_group_and_student_uk UNIQUE (group_id, student_id)
);

CREATE TABLE integrations
(
    id         SERIAL,
    name       VARCHAR(128) NOT NULL,
    project_id BIGINT       NOT NULL,

    CONSTRAINT integrations_pk PRIMARY KEY (id),
    CONSTRAINT integrations_projects_fk FOREIGN KEY (project_id) REFERENCES projects (id),
    CONSTRAINT integrations_name_project_uk UNIQUE (name, project_id)
);

CREATE TABLE stages
(
    id            SERIAL,
    name          VARCHAR(128) NOT NULL,
    start_date    TIMESTAMP,
    end_date      TIMESTAMP,
    points_number INT,
    project_id    BIGINT       NOT NULL,

    CONSTRAINT stages_pk PRIMARY KEY (id),
    CONSTRAINT stages_projects_fk FOREIGN KEY (project_id) REFERENCES projects (id),
    CONSTRAINT stages_name_project_uk UNIQUE (name, project_id),
    CONSTRAINT stages_project_and_order_number UNIQUE (project_id)
);

CREATE TABLE stages_in_integrations
(
    id             SERIAL,
    name           VARCHAR(128) NOT NULL,
    order_number   INT          NOT NULL,
    stage_id       BIGINT       NOT NULL,
    integration_id BIGINT       NOT NULL,

    CONSTRAINT stages_in_integrations_pk PRIMARY KEY (id),
    CONSTRAINT stages_in_integrations_stages_fk FOREIGN KEY (stage_id) REFERENCES stages (id),
    CONSTRAINT stages_in_integrations_integrations_fk FOREIGN KEY (integration_id) REFERENCES integrations (id),
    CONSTRAINT stages_in_integrations_name_uk UNIQUE (name),
    CONSTRAINT stages_in_integrations_integration_id_and_order_number_uk UNIQUE (integration_id, order_number)
);