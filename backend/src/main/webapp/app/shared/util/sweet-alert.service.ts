import { Injectable } from '@angular/core';
import Swal, { SweetAlertIcon, SweetAlertResult } from 'sweetalert2';

export type SweetAlertButtonText = { confirm?: string; cancel?: string };

@Injectable({ providedIn: 'root' })
export class SweetAlertService {
  success(title: string, message: string, timer = 3000): void {
    void Swal.fire({
      icon: 'success',
      title,
      text: message,
      timer,
      timerProgressBar: true,
      showConfirmButton: true,
      confirmButtonText: 'Tamam',
    });
  }

  error(title: string, message: string): void {
    void Swal.fire({
      icon: 'error',
      title,
      text: message,
      confirmButtonText: 'Tamam',
    });
  }

  warning(title: string, message: string): void {
    void Swal.fire({
      icon: 'warning',
      title,
      text: message,
      confirmButtonText: 'Tamam',
    });
  }

  info(title: string, message: string): void {
    void Swal.fire({
      icon: 'info',
      title,
      text: message,
      confirmButtonText: 'Tamam',
    });
  }

  confirm(title: string, message: string, confirmButtonText = 'Evet', cancelButtonText = 'İptal'): Promise<SweetAlertResult> {
    return Swal.fire({
      icon: 'warning',
      title,
      text: message,
      showCancelButton: true,
      confirmButtonText,
      cancelButtonText,
      reverseButtons: true,
    });
  }

  custom(icon: SweetAlertIcon, title: string, message: string, buttonText = 'Tamam'): void {
    void Swal.fire({
      icon,
      title,
      text: message,
      confirmButtonText: buttonText,
    });
  }
}
