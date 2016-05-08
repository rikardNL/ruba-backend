create table "Tenants" (
    "id" VARCHAR NOT NULL PRIMARY KEY,
    "name" VARCHAR NOT NULL
);

create table "Users" (
    "id" VARCHAR NOT NULL PRIMARY KEY,
    "email" VARCHAR NOT NULL,
    "first_name" VARCHAR,
    "last_name" VARCHAR,
    "phone" VARCHAR,
    "street_address" VARCHAR,
    "city" VARCHAR,
    "zip_code" VARCHAR,
    "ssn" VARCHAR,
    "role_description" VARCHAR,
    "presentation" VARCHAR,
    "background_image" VARCHAR
);

create table "Teams" (
    "id" VARCHAR NOT NULL PRIMARY KEY,
    "tenant" VARCHAR NOT NULL,
    "name" VARCHAR NOT NULL,
    "image_url" VARCHAR,
    "email" VARCHAR,
    "description" VARCHAR,
    "ambassador" VARCHAR
);

create table "UsersTenants" (
    "user" VARCHAR NOT NULL,
    "tenant" VARCHAR NOT NULL
);

alter table "UsersTenants"
    add constraint "user_tenant_pk"
        primary key("user","tenant");

create table "UsersTeams" (
    "user" VARCHAR NOT NULL,
    "team" VARCHAR NOT NULL
);

alter table "UsersTeams"
    add constraint "user_team_pk"
        primary key("user","team");

create table "UserImages" (
    "user" VARCHAR NOT NULL,
    "url" VARCHAR NOT NULL
);

alter table "UserImages"
    add constraint "pk_users_images"
        primary key("user","url");

alter table "Teams"
    add constraint "ambassador_fk"
        foreign key("ambassador")
        references "Users"("id")
        on update NO ACTION on delete NO ACTION;

alter table "Teams"
    add constraint "team_tenant_fk"
        foreign key("tenant")
        references "Tenants"("id")
        on update NO ACTION on delete NO ACTION;

alter table "UsersTenants"
    add constraint "tenant_user_fk"
        foreign key("tenant")
        references "Tenants"("id")
        on update NO ACTION on delete NO ACTION;

alter table "UsersTenants"
    add constraint "user_tenant_fk"
        foreign key("user")
        references "Users"("id")
        on update NO ACTION on delete NO ACTION;

alter table "UsersTeams"
    add constraint "team_fk"
        foreign key("team")
        references "Teams"("id")
        on update NO ACTION on delete NO ACTION;

alter table "UsersTeams"
    add constraint "ut_user_fk"
        foreign key("user")
        references "Users"("id")
        on update NO ACTION on delete NO ACTION;

alter table "UserImages"
    add constraint "ui_user_fk"
        foreign key("user")
        references "Users"("id")
        on update NO ACTION on delete NO ACTION;
