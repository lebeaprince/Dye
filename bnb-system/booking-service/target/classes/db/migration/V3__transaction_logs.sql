CREATE TABLE IF NOT EXISTS transaction_logs (
  id BIGSERIAL PRIMARY KEY,
  bnb_slug TEXT NOT NULL,
  actor TEXT NULL,
  action TEXT NOT NULL,
  message TEXT NULL,
  room_id BIGINT NULL,
  device_id BIGINT NULL,
  booking_id BIGINT NULL,
  guest_id BIGINT NULL,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX IF NOT EXISTS idx_transaction_logs_bnb_created_at
  ON transaction_logs (bnb_slug, created_at DESC, id DESC);

CREATE INDEX IF NOT EXISTS idx_transaction_logs_bnb_room
  ON transaction_logs (bnb_slug, room_id, created_at DESC, id DESC);

CREATE INDEX IF NOT EXISTS idx_transaction_logs_bnb_device
  ON transaction_logs (bnb_slug, device_id, created_at DESC, id DESC);

CREATE INDEX IF NOT EXISTS idx_transaction_logs_bnb_booking
  ON transaction_logs (bnb_slug, booking_id, created_at DESC, id DESC);
