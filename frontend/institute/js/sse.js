import auth from './auth.js';

/**
 * Resilient Server-Sent Events (SSE) Client.
 * Connects to live feeds, handles automatic reconnections with exponential backoff,
 * and distributes live notifications and updates to registered UI subscribers.
 */
class SseClient {
  constructor() {
    this.eventSource = null;
    this.listeners = new Map();
    this.reconnectAttempts = 0;
    this.maxReconnectDelay = 30000;
    this.baseDelay = 1000;
  }

  connect(endpoint = '/placement/api/live-updates/stream') {
    if (this.eventSource) {
      this.disconnect();
    }

    const token = auth.getToken();
    const url = token ? `${endpoint}?token=${encodeURIComponent(token)}` : endpoint;

    this.eventSource = new EventSource(url);

    this.eventSource.onopen = () => {
      this.reconnectAttempts = 0;
      this.emit('connectionChange', { status: 'connected' });
    };

    this.eventSource.onmessage = (event) => {
      try {
        const payload = JSON.parse(event.data);
        this.emit('message', payload);
      } catch (e) {
        this.emit('message', event.data);
      }
    };

    this.eventSource.onerror = () => {
      this.disconnect();
      this.emit('connectionChange', { status: 'reconnecting' });
      this.scheduleReconnect(endpoint);
    };
  }

  scheduleReconnect(endpoint) {
    const delay = Math.min(
      this.baseDelay * Math.pow(2, this.reconnectAttempts),
      this.maxReconnectDelay
    );
    this.reconnectAttempts++;
    setTimeout(() => this.connect(endpoint), delay);
  }

  disconnect() {
    if (this.eventSource) {
      this.eventSource.close();
      this.eventSource = null;
      this.emit('connectionChange', { status: 'disconnected' });
    }
  }

  on(eventType, callback) {
    if (!this.listeners.has(eventType)) {
      this.listeners.set(eventType, new Set());
    }
    this.listeners.get(eventType).add(callback);
    return () => this.off(eventType, callback);
  }

  off(eventType, callback) {
    if (this.listeners.has(eventType)) {
      this.listeners.get(eventType).delete(callback);
    }
  }

  emit(eventType, data) {
    if (this.listeners.has(eventType)) {
      this.listeners.get(eventType).forEach(cb => {
        try { cb(data); } catch (err) { console.error('SSE listener error:', err); }
      });
    }
  }
}

const sse = new SseClient();
export default sse;
