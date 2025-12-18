CREATE TABLE IF NOT EXISTS rooms (
  id BIGSERIAL PRIMARY KEY,
  bnb_slug TEXT NOT NULL,
  room_number TEXT NOT NULL,
  smart_lock_id TEXT NOT NULL,
  active BOOLEAN NOT NULL,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  CONSTRAINT uq_rooms_bnb_room_number UNIQUE (bnb_slug, room_number)
);

CREATE INDEX IF NOT EXISTS idx_rooms_bnb_slug ON rooms (bnb_slug);

CREATE TABLE IF NOT EXISTS guests (
  id BIGSERIAL PRIMARY KEY,
  bnb_slug TEXT NOT NULL,
  full_name TEXT NOT NULL,
  phone_number TEXT NOT NULL,
  sms_opt_in BOOLEAN NOT NULL,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX IF NOT EXISTS idx_guests_bnb_slug ON guests (bnb_slug);

CREATE TABLE IF NOT EXISTS bookings (
  id BIGSERIAL PRIMARY KEY,
  bnb_slug TEXT NOT NULL,
  room_id BIGINT NOT NULL,
  guest_id BIGINT NOT NULL,
  start_at TIMESTAMPTZ NOT NULL,
  end_at TIMESTAMPTZ NOT NULL,
  stay_duration TEXT NOT NULL,
  duration_units INT NOT NULL,
  service_package TEXT NOT NULL,
  status TEXT NOT NULL,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  CONSTRAINT fk_bookings_room FOREIGN KEY (room_id) REFERENCES rooms(id) ON DELETE RESTRICT,
  CONSTRAINT fk_bookings_guest FOREIGN KEY (guest_id) REFERENCES guests(id) ON DELETE RESTRICT
);

CREATE INDEX IF NOT EXISTS idx_bookings_bnb_slug ON bookings (bnb_slug);

CREATE TABLE IF NOT EXISTS smart_devices (
  id BIGSERIAL PRIMARY KEY,
  bnb_slug TEXT NOT NULL,
  room_id BIGINT NOT NULL,
  device_type TEXT NOT NULL,
  name TEXT NOT NULL,
  external_id TEXT NOT NULL,
  active BOOLEAN NOT NULL,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  CONSTRAINT fk_devices_room FOREIGN KEY (room_id) REFERENCES rooms(id) ON DELETE CASCADE,
  CONSTRAINT uq_devices_bnb_external_id UNIQUE (bnb_slug, external_id)
);

CREATE INDEX IF NOT EXISTS idx_devices_bnb_slug ON smart_devices (bnb_slug);
CREATE INDEX IF NOT EXISTS idx_devices_bnb_room ON smart_devices (bnb_slug, room_id);
