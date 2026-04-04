-- Book 테이블 생성
CREATE TABLE IF NOT EXISTS book
(
    id                UUID PRIMARY KEY,
    created_at        TIMESTAMP,
    last_modified_at  TIMESTAMP,
    title             VARCHAR(255),
    isbn              VARCHAR(255) NOT NULL,
    author            VARCHAR(255),
    thumbnail_image_url TEXT,
    description       TEXT,
    external_link_url TEXT
);

CREATE INDEX IF NOT EXISTS idx_book_isbn ON book (isbn);
CREATE INDEX IF NOT EXISTS idx_book_title ON book (title);
