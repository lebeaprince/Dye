const api = {
  booking: {
    rooms: () => fetch('/api/booking/rooms').then(r => r.ok ? r.json() : Promise.reject(r)),
    createRoom: (payload) => fetch('/api/booking/rooms', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload),
    }).then(r => r.ok ? r.json() : r.text().then(t => Promise.reject(t))),
    devices: {
      list: () => fetch('/api/booking/devices').then(r => r.ok ? r.json() : Promise.reject(r)),
      create: (payload) => fetch('/api/booking/devices', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload),
      }).then(r => r.ok ? r.json() : r.text().then(t => Promise.reject(t))),
      remove: (id) => fetch(`/api/booking/devices/${id}`, { method: 'DELETE' })
        .then(r => r.ok ? r.json() : r.text().then(t => Promise.reject(t))),
    }
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

function setStatus(id, text, isError = false) {
  const node = document.getElementById(id);
  node.textContent = text;
  node.style.color = isError ? '#ff7b7b' : '';
}

async function refreshRooms() {
  const rooms = await api.booking.rooms().catch(() => []);
  const sel = document.getElementById('deviceRoomId');
  sel.innerHTML = '';
  for (const r of rooms) {
    sel.appendChild(el('option', { value: String(r.id) }, [`Room ${r.roomNumber}`]));
  }
}

async function refreshDevices() {
  const devices = await api.booking.devices.list().catch(() => []);
  const list = document.getElementById('devicesList');
  const empty = document.getElementById('devicesEmpty');
  list.innerHTML = '';

  if (!devices || devices.length === 0) {
    empty.hidden = false;
    return;
  }
  empty.hidden = true;

  for (const d of devices) {
    const meta = el('div', { class: 'item__meta' }, [
      el('div', { class: 'item__title' }, [d.name]),
      el('div', { class: 'item__sub' }, [`${d.deviceType} · roomId=${d.roomId} · extId=${d.externalId}`]),
    ]);
    const btn = el('button', { class: 'btn btn--secondary', type: 'button' }, ['Remove']);
    btn.addEventListener('click', async () => {
      setStatus('deviceStatus', 'Removing...');
      try {
        await api.booking.devices.remove(d.id);
        setStatus('deviceStatus', 'Removed');
        refreshDevices();
      } catch (e) {
        setStatus('deviceStatus', String(e || 'Failed to remove'), true);
      }
    });
    list.appendChild(el('div', { class: 'item' }, [meta, btn]));
  }
}

async function onSubmitRoom(e) {
  e.preventDefault();
  setStatus('roomStatus', 'Creating...');
  try {
    await api.booking.createRoom({
      roomNumber: document.getElementById('roomNumber').value.trim(),
      smartLockId: document.getElementById('smartLockId').value.trim(),
    });
    setStatus('roomStatus', 'Room created');
    e.target.reset();
    await refreshRooms();
  } catch (err) {
    setStatus('roomStatus', String(err || 'Failed to create room'), true);
  }
}

async function onSubmitDevice(e) {
  e.preventDefault();
  setStatus('deviceStatus', 'Adding...');
  try {
    await api.booking.devices.create({
      roomId: Number(document.getElementById('deviceRoomId').value),
      deviceType: document.getElementById('deviceType').value,
      name: document.getElementById('deviceName').value.trim(),
      externalId: document.getElementById('deviceExternalId').value.trim(),
    });
    setStatus('deviceStatus', 'Device added');
    e.target.reset();
    await refreshDevices();
  } catch (err) {
    setStatus('deviceStatus', String(err || 'Failed to add device'), true);
  }
}

(function init() {
  document.getElementById('roomForm').addEventListener('submit', onSubmitRoom);
  document.getElementById('deviceForm').addEventListener('submit', onSubmitDevice);

  // placeholder login form (no backend yet)
  document.getElementById('loginForm').addEventListener('submit', (e) => {
    e.preventDefault();
    setStatus('loginStatus', 'Login not wired yet (placeholder).');
  });

  refreshRooms().then(refreshDevices);
})();
