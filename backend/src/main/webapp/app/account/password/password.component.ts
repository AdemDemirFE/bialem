import { Component, Injector, OnInit, Signal, inject, signal } from '@angular/core';
import { FormControl, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { toSignal } from '@angular/core/rxjs-interop';

import SharedModule from 'app/shared/shared.module';
import { AccountService } from 'app/core/auth/account.service';
import { Account } from 'app/core/auth/account.model';
import { PasswordService } from './password.service';
import { SweetAlertService } from 'app/shared/util/sweet-alert.service';
import PasswordStrengthBarComponent from './password-strength-bar/password-strength-bar.component';

@Component({
  selector: 'jhi-password',
  imports: [SharedModule, FormsModule, ReactiveFormsModule, PasswordStrengthBarComponent],
  templateUrl: './password.component.html',
})
export default class PasswordComponent implements OnInit {
  account?: Signal<Account | undefined | null>;
  passwordForm = new FormGroup({
    currentPassword: new FormControl('', { nonNullable: true, validators: Validators.required }),
    newPassword: new FormControl('', {
      nonNullable: true,
      validators: [Validators.required, Validators.minLength(4), Validators.maxLength(50)],
    }),
    confirmPassword: new FormControl('', {
      nonNullable: true,
      validators: [Validators.required, Validators.minLength(4), Validators.maxLength(50)],
    }),
  });

  currentPasswordVisible = signal(false);
  newPasswordVisible = signal(false);
  confirmPasswordVisible = signal(false);

  private readonly passwordService = inject(PasswordService);
  private readonly accountService = inject(AccountService);
  private readonly sweetAlertService = inject(SweetAlertService);
  private readonly injector = inject(Injector);

  ngOnInit(): void {
    const account$ = this.accountService.identity();
    this.account = toSignal(account$, { injector: this.injector });
  }

  togglePasswordVisibility(field: 'currentPassword' | 'newPassword' | 'confirmPassword'): void {
    if (field === 'currentPassword') {
      this.currentPasswordVisible.update(value => !value);
    } else if (field === 'newPassword') {
      this.newPasswordVisible.update(value => !value);
    } else {
      this.confirmPasswordVisible.update(value => !value);
    }
  }

  changePassword(): void {
    const { newPassword, confirmPassword, currentPassword } = this.passwordForm.getRawValue();

    if (newPassword !== confirmPassword) {
      this.sweetAlertService.error('Şifreler Eşleşmiyor', 'Yeni şifre ve şifre tekrarı aynı olmalıdır.');
      return;
    }

    this.passwordService.save(newPassword, currentPassword).subscribe({
      next: () => {
        this.sweetAlertService.success('Şifre Değiştirildi', 'Şifreniz başarıyla güncellendi.');
        this.passwordForm.reset();
      },
      error: (response: { error?: { detail?: string; title?: string; message?: string } }) => {
        const message = this.extractErrorMessage(response);
        this.sweetAlertService.error('Şifre Değiştirilemedi', message);
      },
    });
  }

  private extractErrorMessage(response: { error?: { detail?: string; title?: string; message?: string } }): string {
    const errorBody = response?.error;
    if (errorBody?.detail) {
      return errorBody.detail;
    }
    if (errorBody?.title && errorBody.title !== 'Internal Server Error') {
      return errorBody.title;
    }
    return 'Şifre değiştirilemedi. Bilgilerinizi kontrol ederek tekrar deneyin.';
  }
}
