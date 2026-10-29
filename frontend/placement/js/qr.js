/**
 * QR Code Scanner & Check-in Helper.
 * Handles webcam/camera stream access, BarcodeDetector API, and manual check-in code fallback.
 */

class QrScannerHelper {
  constructor() {
    this.stream = null;
    this.scanning = false;
  }

  async isCameraSupported() {
    return !!(navigator.mediaDevices && navigator.mediaDevices.getUserMedia);
  }

  async startScanner(videoElement, onCodeDetected) {
    if (!await this.isCameraSupported()) {
      throw new Error('Camera access not supported in this browser.');
    }

    try {
      this.stream = await navigator.mediaDevices.getUserMedia({
        video: { facingMode: 'environment' }
      });
      videoElement.srcObject = this.stream;
      await videoElement.play();
      this.scanning = true;

      if ('BarcodeDetector' in window) {
        const barcodeDetector = new window.BarcodeDetector({ formats: ['qr_code'] });
        const scanLoop = async () => {
          if (!this.scanning) return;
          try {
            const barcodes = await barcodeDetector.detect(videoElement);
            if (barcodes.length > 0) {
              const code = barcodes[0].rawValue;
              this.stopScanner();
              onCodeDetected(code);
              return;
            }
          } catch (e) {
            // Processing frame
          }
          requestAnimationFrame(scanLoop);
        };
        requestAnimationFrame(scanLoop);
      }
    } catch (err) {
      this.stopScanner();
      throw err;
    }
  }

  stopScanner() {
    this.scanning = false;
    if (this.stream) {
      this.stream.getTracks().forEach(track => track.stop());
      this.stream = null;
    }
  }
}

const qr = new QrScannerHelper();
export default qr;
