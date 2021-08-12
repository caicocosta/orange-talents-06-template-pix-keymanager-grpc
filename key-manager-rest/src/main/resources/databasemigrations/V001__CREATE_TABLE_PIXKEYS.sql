CREATE TABLE pix_keys (
  id UUID PRIMARY KEY NOT NULL,
  client_id UUID NOT NULL,
  key varchar(77) UNIQUE,
  type_key varchar(20) NOT NULL,
  type_account varchar(30) NOT NULL
);