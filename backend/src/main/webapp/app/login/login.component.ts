import { AfterViewInit, Component, ElementRef, OnInit, inject, signal, viewChild } from '@angular/core';
import { FormControl, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';

import SharedModule from 'app/shared/shared.module';
import { LoginService } from 'app/login/login.service';
import { AccountService } from 'app/core/auth/account.service';
import { SweetAlertService } from 'app/shared/util/sweet-alert.service';

@Component({
  selector: 'jhi-login',
  imports: [SharedModule, FormsModule, ReactiveFormsModule, RouterModule],
  templateUrl: './login.component.html',
})
export default class LoginComponent implements OnInit, AfterViewInit {
  username = viewChild.required<ElementRef>('username');

  authenticationError = signal(false);
  authenticating = signal(false);

  loginForm = new FormGroup({
    username: new FormControl('', { nonNullable: true, validators: [Validators.required] }),
    password: new FormControl('', { nonNullable: true, validators: [Validators.required] }),
    rememberMe: new FormControl(false, { nonNullable: true, validators: [Validators.required] }),
  });

  private readonly accountService = inject(AccountService);
  private readonly loginService = inject(LoginService);
  private readonly sweetAlertService = inject(SweetAlertService);
  private readonly router = inject(Router);

  ngOnInit(): void {
    this.accountService.identity().subscribe(() => {
      if (this.accountService.isAuthenticated()) {
        this.router.navigate(['']);
      }
    });
  }

  ngAfterViewInit(): void {
    this.username().nativeElement.focus();
  }

  login(): void {
    this.authenticationError.set(false);
    this.authenticating.set(true);

    this.loginService.login(this.loginForm.getRawValue()).subscribe({
      next: () => {
        this.authenticationError.set(false);
        this.authenticating.set(false);
        if (!this.router.getCurrentNavigation()) {
          this.router.navigate(['']);
        }
      },
      error: (error: unknown) => {
        this.authenticationError.set(true);
        this.authenticating.set(false);
        this.showLoginError(error);
      },
    });
  }

  private showLoginError(error: unknown): void {
    if (error instanceof HttpErrorResponse) {
      const status = error.status;
      const problem = error.error;

      if (status === 401 || status === 403) {
        this.sweetAlertService.error('Giriş Başarısız', 'Kullanıcı adı/e-posta veya şifre hatalı.');
        return;
      }

      if (status >= 500 && status < 600) {
        this.sweetAlertService.error('Sunucu Hatası', 'Sunucuya şu anda erişilemiyor. Lütfen kısa süre sonra tekrar deneyin.');
        return;
      }

      if (problem && typeof problem === 'object') {
        const detail = (problem as { detail?: string }).detail;
        const title = (problem as { title?: string }).title;
        const message = detail || (title && title !== 'Internal Server Error' ? title : undefined);
        if (message) {
          this.sweetAlertService.error('Giriş Yapılamadı', message);
          return;
        }
      }
    }

    if (this.isNetworkError(error)) {
      this.sweetAlertService.error('Bağlantı Hatası', 'Sunucuya bağlantı kurulamadı. İnternet bağlantınızı kontrol edip tekrar deneyin.');
      return;
    }

    this.sweetAlertService.error('Giriş Yapılamadı', 'Beklenmeyen bir hata oluştu. Lütfen tekrar deneyin.');
  }

  private isNetworkError(error: unknown): boolean {
    if (error instanceof HttpErrorResponse) {
      return error.status === 0 || error.error instanceof ErrorEvent || error.message?.toLowerCase().includes('network');
    }
    if (error instanceof Error) {
      const message = error.message.toLowerCase();
      return (
        message.includes('network') ||
        message.includes('connection') ||
        message.includes('connect') ||
        message.includes('timeout') ||
        message.includes('refused')
      );
    }
    return false;
  }
}
