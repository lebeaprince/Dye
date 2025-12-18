const api = {
  booking: {
    rooms: () => fetch('/api/booking/rooms').then(r => r.ok ? r.json() : Promise.reject(r)),
    createGuest: (payload) => fetch('/api/booking/guests', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload),
    }).then(r => r.ok ? r.json() : r.text().then(t => Promise.reject(t))),
    createBooking: (payload) => fetch('/api/booking/bookings', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload),
    }).then(r => r.ok ? r.json() : r.text().then(t => Promise.reject(t))),
  }
};

function el(tag, attrs = {}, children = []) {
  const node = document.createElement(tag);
  Object.entries(attrs).forEach(([k, v]) => {
    if (k === 'class') node.className = v;
    else if (k === 'html') node.innerHTML = v;
    else node.setAttribute(k, v);
  });
  children.forEach(c => node.appendChild(typeof c === 'string' ? document.createTextNode(c) : c));
  return node;
}

async function loadRooms() {
  const roomsList = document.getElementById('roomsList');
  const roomsEmpty = document.getElementById('roomsEmpty');
  const roomId = document.getElementById('roomId');

  roomsList.innerHTML = '';
  roomId.innerHTML = '';

  const rooms = await api.booking.rooms().catch(() => []);

  if (!rooms || rooms.length === 0) {
    roomsEmpty.hidden = false;
    return;
  }

  roomsEmpty.hidden = true;

  for (const r of rooms) {
    roomsList.appendChild(el('div', { class: 'card' }, [
      el('div', { class: 'item__meta' }, [
        el('div', { class: 'item__title' }, [`Room ${r.roomNumber}`]),
        el('div', { class: 'item__sub' }, [`Smart lock: ${r.smartLockId}`])
      ]),
      el('div', { class: 'muted' }, [r.active ? 'Active' : 'Inactive'])
    ]));

    roomId.appendChild(el('option', { value: String(r.id) }, [`Room ${r.roomNumber}`]));
  }
}

function toOffsetDateTimeUtc(datetimeLocalValue) {
  // datetime-local has no timezone; interpret as UTC for simplicity.
  // Produces ISO-8601 with Z.
  const d = new Date(datetimeLocalValue);
  return new Date(Date.UTC(
    d.getFullYear(),
    d.getMonth(),
    d.getDate(),
    d.getHours(),
    d.getMinutes(),
    0,
    0
  )).toISOString();
}

function setStatus(id, text, isError = false) {
  const node = document.getElementById(id);
  node.textContent = text;
  node.style.color = isError ? '#ff7b7b' : '';
}

async function onSubmitBooking(e) {
  e.preventDefault();

  setStatus('bookingStatus', 'Submitting...');

  try {
    const guest = await api.booking.createGuest({
      fullName: document.getElementById('fullName').value.trim(),
      phoneNumber: document.getElementById('phone').value.trim(),
      smsOptIn: document.getElementById('smsOptIn').checked,
    });

    const booking = await api.booking.createBooking({
      roomId: Number(document.getElementById('roomId').value),
      guestId: Number(guest.id),
      startAt: toOffsetDateTimeUtc(document.getElementById('startAt').value),
      stayDuration: document.getElementById('stayDuration').value,
      durationUnits: Number(document.getElementById('durationUnits').value),
      servicePackage: document.getElementById('servicePackage').value,
    });

    setStatus('bookingStatus', `Booking requested! Reference: ${booking.id} (status: ${booking.status})`);
    e.target.reset();
    document.getElementById('smsOptIn').checked = true;
  } catch (err) {
    setStatus('bookingStatus', String(err || 'Failed to book'), true);
  }
}

(function init() {
  document.getElementById('year').textContent = String(new Date().getFullYear());

  const start = document.getElementById('startAt');
  const now = new Date();
  now.setMinutes(now.getMinutes() - now.getTimezoneOffset());
  start.value = now.toISOString().slice(0, 16);

  document.getElementById('bookingForm').addEventListener('submit', onSubmitBooking);
  loadRooms();
})();
