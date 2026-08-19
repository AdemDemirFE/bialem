import { Routes } from '@angular/router';

export const errorRoute: Routes = [
  {
    path: 'error',
    loadComponent: () => import('./error.component'),
    title: 'Hata Sayfası!',
  },
  {
    path: 'accessdenied',
    loadComponent: () => import('./error.component'),
    data: {
      errorMessage: 'Bu sayfaya erişmeye yetkiniz yoktur.',
    },
    title: 'Hata Sayfası!',
  },
  {
    path: '404',
    loadComponent: () => import('./error.component'),
    data: {
      errorMessage: 'Bu sayfa mevcut değil.',
    },
    title: 'Hata Sayfası!',
  },
  {
    path: '**',
    redirectTo: '/404',
  },
];
