/* country stats table */
CREATE TABLE statsPaymentPerCountry (
  id INTEGER PRIMARY KEY AUTOINCREMENT, /* sqlite autoincrement support only integer */
  ts DATETIME DEFAULT CURRENT_TIMESTAMP,
  country VARCHAR(255),
  userCount BIGINT,
  payment BIGINT
);

/* set index by ts on statsCountry */
CREATE INDEX idxCountTs ON statsPaymentPerCountry(ts);

/* payment stats table */
CREATE TABLE statsPaymentPerGender (
  id INTEGER PRIMARY KEY AUTOINCREMENT, /* sqlite autoincrement support only integer */
  ts DATETIME DEFAULT CURRENT_TIMESTAMP,
  gender VARCHAR(36),
  userCount BIGINT,
  payment BIGINT
);

/* set index by ts on statsPaymentPerGender */
CREATE INDEX idxGenderTs ON statsPaymentPerGender(ts);