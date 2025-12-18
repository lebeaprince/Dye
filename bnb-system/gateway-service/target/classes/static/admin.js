const api = {
  booking: {
    rooms: () => fetch('/api/booking/rooms').then(r => r.ok ? r.json() : Promise.reject(r)),
    createRoom: (payload) => fetch('/api/booking/rooms', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload),
    }).then(r => r.ok ? r.json() : r.text().then(t => Promise.reject(t))),
    dashboard: () => fetch('/api/booking/dashboard').then(r => r.ok ? r.json() : Promise.reject(r)),
    booking: {
      get: (id) => fetch(`/api/booking/bookings/${id}`).then(r => r.ok ? r.json() : r.text().then(t => Promise.reject(t))),
      confirm: (id) => fetch(`/api/booking/bookings/${id}/confirm`, { method: 'POST' })
        .then(r => r.ok ? r.json() : r.text().then(t => Promise.reject(t))),
      cancel: (id) => fetch(`/api/booking/bookings/${id}/cancel`, { method: 'POST' })
        .then(r => r.ok ? r.json() : r.text().then(t => Promise.reject(t))),
    },
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
  },
  payment: {
    create: (payload) => fetch('/api/payment/payments', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload),
    }).then(r => r.ok ? r.json() : r.text().then(t => Promise.reject(t))),
    get: (id) => fetch(`/api/payment/payments/${id}`).then(r => r.ok ? r.json() : r.text().then(t => Promise.reject(t))),
    listByBooking: (bookingId) => fetch(`/api/payment/payments?bookingId=${encodeURIComponent(String(bookingId))}`)
      .then(r => r.ok ? r.json() : r.text().then(t => Promise.reject(t))),
    capture: (id) => fetch(`/api/payment/payments/${id}/capture`, { method: 'POST' })
      .then(r => r.ok ? r.json() : r.text().then(t => Promise.reject(t))),
    cancel: (id) => fetch(`/api/payment/payments/${id}/cancel`, { method: 'POST' })
      .then(r => r.ok ? r.json() : r.text().then(t => Promise.reject(t))),
  },
  access: {
    grant: (payload) => fetch('/api/access/access/grants', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload),
    }).then(r => r.ok ? r.json() : r.text().then(t => Promise.reject(t))),
    get: (grantId) => fetch(`/api/access/access/grants/${grantId}`)
      .then(r => r.ok ? r.json() : r.text().then(t => Promise.reject(t))),
    revoke: (grantId) => fetch(`/api/access/access/grants/${grantId}/revoke`, { method: 'POST' })
      .then(r => r.ok ? r.json() : r.text().then(t => Promise.reject(t))),
  },
  notification: {
    sms: {
      send: (payload) => fetch('/api/notification/sms', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload),
      }).then(r => r.ok ? r.json() : r.text().then(t => Promise.reject(t))),
      list: () => fetch('/api/notification/sms').then(r => r.ok ? r.json() : r.text().then(t => Promise.reject(t))),
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

function setPre(id, value) {
  const node = document.getElementById(id);
  node.textContent = typeof value === 'string' ? value : JSON.stringify(value, null, 2);
}

function toOffsetDateTimeUtc(datetimeLocalValue) {
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

function fmtTs(ts) {
  try {
    return new Date(ts).toLocaleString();
  } catch {
    return String(ts || '');
  }
}

function renderDashboard(dashboard) {
  const counts = document.getElementById('txCounts');
  const countsEmpty = document.getElementById('txCountsEmpty');
  const recent = document.getElementById('txRecent');
  const recentEmpty = document.getElementById('txRecentEmpty');

  counts.innerHTML = '';
  recent.innerHTML = '';

  const actionCounts = (dashboard && dashboard.actionCountsLast24h) || [];
  const recents = (dashboard && dashboard.recent) || [];

  if (actionCounts.length === 0) {
    countsEmpty.hidden = false;
  } else {
    countsEmpty.hidden = true;
    for (const c of actionCounts) {
      counts.appendChild(el('div', { class: 'item' }, [
        el('div', { class: 'item__meta' }, [
          el('div', { class: 'item__title' }, [c.action]),
          el('div', { class: 'item__sub' }, [`Count: ${c.count}`]),
        ]),
      ]));
    }
  }

  if (recents.length === 0) {
    recentEmpty.hidden = false;
  } else {
    recentEmpty.hidden = true;
    for (const t of recents) {
      const sub = [];
      if (t.roomId != null) sub.push(`roomId=${t.roomId}`);
      if (t.deviceId != null) sub.push(`deviceId=${t.deviceId}`);
      if (t.bookingId != null) sub.push(`bookingId=${t.bookingId}`);
      if (t.guestId != null) sub.push(`guestId=${t.guestId}`);
      const actor = t.actor ? ` · ${t.actor}` : '';
      const details = sub.length ? ` · ${sub.join(' · ')}` : '';
      recent.appendChild(el('div', { class: 'item' }, [
        el('div', { class: 'item__meta' }, [
          el('div', { class: 'item__title' }, [t.action]),
          el('div', { class: 'item__sub' }, [`${fmtTs(t.createdAt)}${actor}${details}`]),
          t.message ? el('div', { class: 'muted' }, [t.message]) : el('div', { class: 'muted' }, ['']),
        ]),
      ]));
    }
  }
}

async function refreshDashboard() {
  const data = await api.booking.dashboard().catch(() => null);
  renderDashboard(data);
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
        refreshDashboard();
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
    await refreshDashboard();
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
    await refreshDashboard();
  } catch (err) {
    setStatus('deviceStatus', String(err || 'Failed to add device'), true);
  }
}

async function onAdminBookingGet() {
  const id = Number(document.getElementById('adminBookingId').value);
  if (!id) return;
  setStatus('adminBookingStatus', 'Loading...');
  setPre('adminBookingResult', '');
  try {
    const booking = await api.booking.booking.get(id);
    setStatus('adminBookingStatus', 'OK');
    setPre('adminBookingResult', booking);
  } catch (e) {
    setStatus('adminBookingStatus', String(e || 'Failed'), true);
  }
}

async function onAdminBookingConfirm() {
  const id = Number(document.getElementById('adminBookingId').value);
  if (!id) return;
  setStatus('adminBookingStatus', 'Confirming...');
  setPre('adminBookingResult', '');
  try {
    const booking = await api.booking.booking.confirm(id);
    setStatus('adminBookingStatus', 'Confirmed');
    setPre('adminBookingResult', booking);
    refreshDashboard();
  } catch (e) {
    setStatus('adminBookingStatus', String(e || 'Failed'), true);
  }
}

async function onAdminBookingCancel() {
  const id = Number(document.getElementById('adminBookingId').value);
  if (!id) return;
  setStatus('adminBookingStatus', 'Cancelling...');
  setPre('adminBookingResult', '');
  try {
    const booking = await api.booking.booking.cancel(id);
    setStatus('adminBookingStatus', 'Cancelled');
    setPre('adminBookingResult', booking);
    refreshDashboard();
  } catch (e) {
    setStatus('adminBookingStatus', String(e || 'Failed'), true);
  }
}

async function onSubmitPaymentCreate(e) {
  e.preventDefault();
  setStatus('paymentCreateStatus', 'Creating...');
  setPre('paymentResult', '');
  try {
    const payment = await api.payment.create({
      bookingId: Number(document.getElementById('payBookingId').value),
      amountCents: Number(document.getElementById('payAmountCents').value),
      currency: document.getElementById('payCurrency').value.trim(),
      method: document.getElementById('payMethod').value,
    });
    setStatus('paymentCreateStatus', 'Created');
    setPre('paymentResult', payment);
    document.getElementById('payPaymentId').value = String(payment.id ?? '');
  } catch (e2) {
    setStatus('paymentCreateStatus', String(e2 || 'Failed'), true);
  }
}

async function onPaymentGet() {
  const id = Number(document.getElementById('payPaymentId').value);
  if (!id) return;
  setStatus('paymentActionsStatus', 'Loading...');
  setPre('paymentResult', '');
  try {
    const p = await api.payment.get(id);
    setStatus('paymentActionsStatus', 'OK');
    setPre('paymentResult', p);
  } catch (e) {
    setStatus('paymentActionsStatus', String(e || 'Failed'), true);
  }
}

async function onPaymentList() {
  const bookingId = Number(document.getElementById('payListBookingId').value);
  if (!bookingId) return;
  setStatus('paymentActionsStatus', 'Loading...');
  setPre('paymentResult', '');
  try {
    const list = await api.payment.listByBooking(bookingId);
    setStatus('paymentActionsStatus', `OK (${list.length})`);
    setPre('paymentResult', list);
  } catch (e) {
    setStatus('paymentActionsStatus', String(e || 'Failed'), true);
  }
}

async function onPaymentCapture() {
  const id = Number(document.getElementById('payPaymentId').value);
  if (!id) return;
  setStatus('paymentActionsStatus', 'Capturing + provisioning...');
  setPre('paymentResult', '');
  try {
    const p = await api.payment.capture(id);
    setStatus('paymentActionsStatus', 'Done');
    setPre('paymentResult', p);
    refreshDashboard();
  } catch (e) {
    setStatus('paymentActionsStatus', String(e || 'Failed'), true);
  }
}

async function onPaymentCancel() {
  const id = Number(document.getElementById('payPaymentId').value);
  if (!id) return;
  setStatus('paymentActionsStatus', 'Cancelling...');
  setPre('paymentResult', '');
  try {
    const p = await api.payment.cancel(id);
    setStatus('paymentActionsStatus', 'Cancelled');
    setPre('paymentResult', p);
  } catch (e) {
    setStatus('paymentActionsStatus', String(e || 'Failed'), true);
  }
}

async function onSubmitAccessGrant(e) {
  e.preventDefault();
  setStatus('accessGrantStatus', 'Granting...');
  setPre('accessResult', '');
  try {
    const grant = await api.access.grant({
      bookingId: Number(document.getElementById('grantBookingId').value),
      smartLockId: document.getElementById('grantSmartLockId').value.trim(),
      guestPhoneNumber: document.getElementById('grantGuestPhone').value.trim(),
      validFrom: toOffsetDateTimeUtc(document.getElementById('grantValidFrom').value),
      validTo: toOffsetDateTimeUtc(document.getElementById('grantValidTo').value),
    });
    setStatus('accessGrantStatus', 'Granted');
    setPre('accessResult', grant);
    document.getElementById('grantId').value = String(grant.id ?? '');
  } catch (e2) {
    setStatus('accessGrantStatus', String(e2 || 'Failed'), true);
  }
}

async function onAccessGet() {
  const id = Number(document.getElementById('grantId').value);
  if (!id) return;
  setStatus('accessActionsStatus', 'Loading...');
  setPre('accessResult', '');
  try {
    const g = await api.access.get(id);
    setStatus('accessActionsStatus', 'OK');
    setPre('accessResult', g);
  } catch (e) {
    setStatus('accessActionsStatus', String(e || 'Failed'), true);
  }
}

async function onAccessRevoke() {
  const id = Number(document.getElementById('grantId').value);
  if (!id) return;
  setStatus('accessActionsStatus', 'Revoking...');
  setPre('accessResult', '');
  try {
    const g = await api.access.revoke(id);
    setStatus('accessActionsStatus', 'Revoked');
    setPre('accessResult', g);
  } catch (e) {
    setStatus('accessActionsStatus', String(e || 'Failed'), true);
  }
}

function renderSmsList(items) {
  const list = document.getElementById('smsList');
  const empty = document.getElementById('smsEmpty');
  list.innerHTML = '';

  if (!items || items.length === 0) {
    empty.hidden = false;
    return;
  }
  empty.hidden = true;

  for (const s of items) {
    const meta = el('div', { class: 'item__meta' }, [
      el('div', { class: 'item__title' }, [`To: ${s.to}`]),
      el('div', { class: 'item__sub' }, [`${fmtTs(s.createdAt)} · id=${s.id}`]),
      el('div', { class: 'muted' }, [s.message || '']),
    ]);
    list.appendChild(el('div', { class: 'item' }, [meta]));
  }
}

async function refreshSms() {
  setStatus('smsListStatus', 'Loading...');
  try {
    const items = await api.notification.sms.list();
    setStatus('smsListStatus', `OK (${items.length})`);
    renderSmsList(items);
  } catch (e) {
    setStatus('smsListStatus', String(e || 'Failed'), true);
  }
}

async function onSubmitSmsSend(e) {
  e.preventDefault();
  setStatus('smsSendStatus', 'Sending...');
  try {
    await api.notification.sms.send({
      to: document.getElementById('smsTo').value.trim(),
      message: document.getElementById('smsMessage').value.trim(),
    });
    setStatus('smsSendStatus', 'Sent');
    e.target.reset();
    refreshSms();
  } catch (e2) {
    setStatus('smsSendStatus', String(e2 || 'Failed'), true);
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

  document.getElementById('adminBookingGet').addEventListener('click', onAdminBookingGet);
  document.getElementById('adminBookingConfirm').addEventListener('click', onAdminBookingConfirm);
  document.getElementById('adminBookingCancel').addEventListener('click', onAdminBookingCancel);

  document.getElementById('paymentCreateForm').addEventListener('submit', onSubmitPaymentCreate);
  document.getElementById('paymentGet').addEventListener('click', onPaymentGet);
  document.getElementById('paymentList').addEventListener('click', onPaymentList);
  document.getElementById('paymentCapture').addEventListener('click', onPaymentCapture);
  document.getElementById('paymentCancel').addEventListener('click', onPaymentCancel);

  document.getElementById('accessGrantForm').addEventListener('submit', onSubmitAccessGrant);
  document.getElementById('accessGet').addEventListener('click', onAccessGet);
  document.getElementById('accessRevoke').addEventListener('click', onAccessRevoke);

  document.getElementById('smsSendForm').addEventListener('submit', onSubmitSmsSend);
  document.getElementById('smsRefresh').addEventListener('click', refreshSms);

  const now = new Date();
  now.setMinutes(now.getMinutes() - now.getTimezoneOffset());
  const from = now.toISOString().slice(0, 16);
  const later = new Date(now.getTime() + 24 * 60 * 60 * 1000);
  const to = later.toISOString().slice(0, 16);
  const fromEl = document.getElementById('grantValidFrom');
  const toEl = document.getElementById('grantValidTo');
  if (fromEl && !fromEl.value) fromEl.value = from;
  if (toEl && !toEl.value) toEl.value = to;

  refreshRooms().then(refreshDevices).then(refreshDashboard).then(refreshSms);
})();
