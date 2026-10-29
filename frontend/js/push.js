import api from './api.js';

/**
 * Web Push Subscription Client.
 * Connects browser Service Worker PushManager to backend VAPID endpoints.
 */
class PushManagerClient {
  constructor() {
    this.swRegistration = null;
  }

  async init() {
    if (!('serviceWorker' in navigator) || !('PushManager' in window)) {
      return false;
    }

    try {
      this.swRegistration = await navigator.serviceWorker.register('/sw.js', { scope: '/' });
      return true;
    } catch (err) {
      console.warn('Service worker registration failed:', err);
      return false;
    }
  }

  async isSubscribed() {
    if (!this.swRegistration) await this.init();
    if (!this.swRegistration) return false;
    const sub = await this.swRegistration.pushManager.getSubscription();
    return !!sub;
  }

  async subscribe() {
    if (!this.swRegistration) await this.init();
    if (!this.swRegistration) throw new Error('Push notifications not supported on this browser');

    // Get VAPID public key from server
    const keyData = await api.get('/placement/api/push/public-key');
    const vapidKey = keyData.publicKey || keyData;

    const convertedVapidKey = this.urlBase64ToUint8Array(vapidKey);

    const subscription = await this.swRegistration.pushManager.subscribe({
      userVisibleOnly: true,
      applicationServerKey: convertedVapidKey
    });

    // Send subscription object to backend
    await api.post('/placement/api/push/subscribe', subscription.toJSON());
    return subscription;
  }

  async unsubscribe() {
    if (!this.swRegistration) return;
    const subscription = await this.swRegistration.pushManager.getSubscription();
    if (subscription) {
      await api.delete('/placement/api/push/subscribe');
      await subscription.unsubscribe();
    }
  }

  urlBase64ToUint8Array(base64String) {
    const padding = '='.repeat((4 - base64String.length % 4) % 4);
    const base64 = (base64String + padding)
      .replace(/-/g, '+')
      .replace(/_/g, '/');

    const rawData = window.atob(base64);
    const outputArray = new Uint8Array(rawData.length);

    for (let i = 0; i < rawData.length; ++i) {
      outputArray[i] = rawData.charCodeAt(i);
    }
    return outputArray;
  }
}

const push = new PushManagerClient();
export default push;
