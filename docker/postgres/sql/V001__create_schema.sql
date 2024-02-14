-- \connect "pipegine"

CREATE SCHEMA pipegine_platform;

ALTER SCHEMA pipegine_platform OWNER TO "pipegine";

DROP TABLE IF EXISTS pipegine_platform.project CASCADE;

CREATE TABLE pipegine_platform.project(
    id uuid NOT NULL,
    name varchar NOT NULL,
    description varchar NOT NULL,
    group_id uuid NOT NULL,
    owner_id uuid NOT NULL
);

ALTER TABLE pipegine_platform.project OWNER TO "pipegine";

ALTER TABLE pipegine_platform.project
    ADD CONSTRAINT project_pkey PRIMARY KEY (id);

DROP TABLE IF EXISTS pipegine_platform.dataset CASCADE;

CREATE TABLE pipegine_platform.dataset(
    id uuid NOT NULL,
    filename varchar NOT NULL,
    project_id uuid NOT NULL
);

ALTER TABLE pipegine_platform.dataset OWNER TO "pipegine";

ALTER TABLE pipegine_platform.dataset
    ADD CONSTRAINT dataset_pkey PRIMARY KEY (id);

ALTER TABLE pipegine_platform.dataset
    ADD CONSTRAINT dataset_project_id_fkey FOREIGN KEY (project_id)
        REFERENCES pipegine_platform.project(id) ON DELETE CASCADE;

DROP TABLE IF EXISTS pipegine_platform.provider CASCADE;

CREATE TABLE pipegine_platform.provider(
    id uuid NOT NULL,
    name varchar NOT NULL,
    description varchar NOT NULL,
    url varchar NOT NULL,
    url_source varchar,
    public boolean NOT NULL,
    input_supported_types text,
    output_supported_types text,
    operations jsonb,
    owner_id uuid NOT NULL
);

ALTER TABLE pipegine_platform.provider OWNER TO "pipegine";

ALTER TABLE pipegine_platform.provider
    ADD CONSTRAINT provider_pkey PRIMARY KEY (id);

DROP TYPE IF EXISTS pipegine_platform.pipeline_status CASCADE;

CREATE TYPE pipegine_platform.pipeline_status AS ENUM (
    'DISABLED',
    'ENABLED'
);

ALTER TYPE pipegine_platform.pipeline_status OWNER TO "pipegine";

DROP TABLE IF EXISTS pipegine_platform.pipeline CASCADE;

CREATE TABLE pipegine_platform.pipeline(
   id uuid NOT NULL,
   project_id uuid NOT NULL,
   description varchar NOT NULL,
   status pipegine_platform.pipeline_status
);

ALTER TABLE pipegine_platform.pipeline OWNER TO "pipegine";

ALTER TABLE pipegine_platform.pipeline
    ADD CONSTRAINT pipeline_pkey PRIMARY KEY (id);

ALTER TABLE pipegine_platform.pipeline
    ADD CONSTRAINT pipeline_project_id_fkey FOREIGN KEY (project_id)
        REFERENCES pipegine_platform.project(id) ON DELETE CASCADE;

DROP TABLE IF EXISTS pipegine_platform.pipeline_step CASCADE;

CREATE TABLE pipegine_platform.pipeline_step(
    step_id uuid NOT NULL,
    pipeline_id uuid NOT NULL,
    provider_id uuid NOT NULL,
    input_type varchar NOT NULL,
    output_type varchar NOT NULL,
    params jsonb,
    step_number int
);

ALTER TABLE pipegine_platform.pipeline_step OWNER TO "pipegine";

ALTER TABLE pipegine_platform.pipeline_step
    ADD CONSTRAINT pipeline_step_pkey PRIMARY KEY (step_id);

ALTER TABLE pipegine_platform.pipeline_step
    ADD CONSTRAINT pipeline_step_pipeline_id_fkey FOREIGN KEY (pipeline_id)
        REFERENCES pipegine_platform.pipeline(id) ON DELETE CASCADE;

ALTER TABLE pipegine_platform.pipeline_step
    ADD CONSTRAINT pipeline_step_provider_id_fkey FOREIGN KEY (provider_id)
        REFERENCES pipegine_platform.provider(id) ON DELETE CASCADE;

DROP TYPE IF EXISTS pipegine_platform.execution_status CASCADE;

CREATE TYPE pipegine_platform.execution_status AS ENUM (
    'WAITING',
    'IN_PROGRESS',
    'DONE'
);

ALTER TYPE pipegine_platform.execution_status OWNER TO "pipegine";

DROP TABLE IF EXISTS pipegine_platform.execution CASCADE;

CREATE TABLE pipegine_platform.execution(
    id uuid NOT NULL,
    pipeline_id uuid NOT NULL,
    dataset_id uuid NOT NULL,
    description varchar NOT NULL,
    result varchar,
    current_step integer,
    status pipegine_platform.execution_status
);

ALTER TABLE pipegine_platform.execution OWNER TO "pipegine";

ALTER TABLE pipegine_platform.execution
    ADD CONSTRAINT execution_pkey PRIMARY KEY (id);

ALTER TABLE pipegine_platform.execution
    ADD CONSTRAINT execution_pipeline_id_fkey FOREIGN KEY (pipeline_id)
        REFERENCES pipegine_platform.pipeline(id) ON DELETE CASCADE;

ALTER TABLE pipegine_platform.execution
    ADD CONSTRAINT execution_dataset_id_fkey FOREIGN KEY (dataset_id)
        REFERENCES pipegine_platform.dataset(id) ON DELETE CASCADE;

DROP TYPE IF EXISTS pipegine_platform.execution_step_state CASCADE;

CREATE TYPE pipegine_platform.execution_step_state AS ENUM (
    'SUCCESS',
    'ERROR',
    'NOT_EXECUTED',
    'IN_PROGRESS'
);

ALTER TYPE pipegine_platform.execution_step_state OWNER TO "pipegine";

DROP TABLE IF EXISTS pipegine_platform.execution_step CASCADE;

CREATE TABLE pipegine_platform.execution_step(
    id uuid NOT NULL,
    execution_id uuid NOT NULL,
    provider_id uuid NOT NULL,
    input_type varchar NOT NULL,
    output_type varchar NOT NULL,
    state pipegine_platform.execution_step_state DEFAULT 'NOT_EXECUTED',
    params jsonb,
    step_number int
);

ALTER TABLE pipegine_platform.execution_step OWNER TO "pipegine";

ALTER TABLE pipegine_platform.execution_step
    ADD CONSTRAINT execution_step_pkey PRIMARY KEY (id);

ALTER TABLE pipegine_platform.execution_step
    ADD CONSTRAINT execution_step_execution_id_fkey FOREIGN KEY (execution_id)
        REFERENCES pipegine_platform.execution(id) ON DELETE CASCADE;

ALTER TABLE pipegine_platform.execution_step
    ADD CONSTRAINT execution_step_provider_id_fkey FOREIGN KEY (provider_id) REFERENCES pipegine_platform.provider(id);

DROP TABLE IF EXISTS pipegine_platform.group CASCADE;

CREATE TABLE pipegine_platform.group(
    id uuid NOT NULL,
    owner_id uuid NOT NULL
);

ALTER TABLE pipegine_platform.group OWNER TO "pipegine";

ALTER TABLE pipegine_platform.group
    ADD CONSTRAINT group_pkey PRIMARY KEY (id);

DROP TYPE IF EXISTS pipegine_platform.group_participation_status CASCADE;

CREATE TYPE pipegine_platform.group_participation_status AS ENUM (
    'PENDING',
    'ACCEPTED',
    'REJECTED',
    'EXITED'
);

ALTER TYPE pipegine_platform.group_participation_status OWNER TO "pipegine";

DROP TABLE IF EXISTS pipegine_platform.group_participation CASCADE;

CREATE TABLE pipegine_platform.group_participation(
    id uuid NOT NULL,
    group_id uuid NOT NULL,
    receive_user_id uuid NOT NULL,
    submitter_user_id uuid NOT NULL,
    create_date timestamp NOT NULL,
    status pipegine_platform.group_participation_status
);

ALTER TABLE pipegine_platform.group_participation OWNER TO "pipegine";

ALTER TABLE pipegine_platform.group_participation
    ADD CONSTRAINT group_participation_pkey PRIMARY KEY (id);

ALTER TABLE pipegine_platform.group_participation
    ADD CONSTRAINT group_participation_group_id_fkey FOREIGN KEY (group_id)
        REFERENCES pipegine_platform.group(id) ON DELETE CASCADE;

DROP TABLE IF EXISTS pipegine_platform.application_user CASCADE;

CREATE TABLE pipegine_platform.application_user(
    id uuid NOT NULL,
    name varchar NOT NULL,
    username varchar NOT NULL UNIQUE,
    password varchar NOT NULL,
    orcid varchar,
    github varchar,
    is_account_non_expired boolean default false,
    is_account_nonLocked boolean default false,
    is_credentials_non_expired boolean default false,
    is_enabled boolean default false
);

DROP TABLE IF EXISTS pipegine_platform.group_provider CASCADE;

CREATE TABLE pipegine_platform.group_provider(
    group_id uuid NOT NULL,
    provider_id uuid NOT NULL
);

ALTER TABLE pipegine_platform.group_provider OWNER TO "pipegine";

ALTER TABLE pipegine_platform.group_provider
    ADD CONSTRAINT group_provider_pkey PRIMARY KEY (group_id, provider_id);

ALTER TABLE pipegine_platform.group_provider
    ADD CONSTRAINT group_provider_group_id_fkey FOREIGN KEY (group_id)
        REFERENCES pipegine_platform.group(id) ON DELETE CASCADE;

ALTER TABLE pipegine_platform.group_provider
    ADD CONSTRAINT group_provider_provider_id_fkey FOREIGN KEY (provider_id)
        REFERENCES pipegine_platform.provider(id) ON DELETE CASCADE;

ALTER TABLE pipegine_platform.application_user OWNER TO "pipegine";

ALTER TABLE pipegine_platform.application_user
    ADD CONSTRAINT application_user_pkey PRIMARY KEY (id);

ALTER TABLE pipegine_platform.project
    ADD CONSTRAINT project_owner_id_fkey FOREIGN KEY (owner_id)
        REFERENCES pipegine_platform.application_user(id) ON DELETE CASCADE;

ALTER TABLE pipegine_platform.provider
    ADD CONSTRAINT provider_owner_id_fkey FOREIGN KEY (owner_id) REFERENCES pipegine_platform.application_user(id);

ALTER TABLE pipegine_platform.group_participation
    ADD CONSTRAINT group_participation_receive_user_id_fkey FOREIGN KEY (receive_user_id)
        REFERENCES pipegine_platform.application_user(id) ON DELETE CASCADE;

ALTER TABLE pipegine_platform.group_participation
    ADD CONSTRAINT group_participation_submitter_user_id_fkey FOREIGN KEY (submitter_user_id)
        REFERENCES pipegine_platform.application_user(id) ON DELETE CASCADE;

ALTER TABLE pipegine_platform.project
    ADD CONSTRAINT project_group_id_fkey FOREIGN KEY (group_id)
        REFERENCES pipegine_platform.group(id) ON DELETE CASCADE;