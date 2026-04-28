import { createApi } from '@reduxjs/toolkit/query/react';
import { baseQueryWithReauth } from '@/app/baseQuery';
import type { PaymentRequest, PaymentResponse, InvoiceResponse } from '@/types/fee.types';

export const feeApi = createApi({
  reducerPath: 'feeApi',
  baseQuery: baseQueryWithReauth,
  tagTypes: ['Invoice'],
  endpoints: (builder) => ({
    payInvoice: builder.mutation<PaymentResponse, PaymentRequest>({
      query: (body) => ({ url: '/api/fees/pay', method: 'POST', body }),
      invalidatesTags: ['Invoice'],
    }),
    getStudentInvoices: builder.query<InvoiceResponse[], string>({
      query: (studentId) => `/api/fees/invoices/student/${studentId}`,
      providesTags: ['Invoice'],
    }),
    getOutstandingInvoices: builder.query<InvoiceResponse[], void>({
      query: () => '/api/fees/invoices/outstanding',
      providesTags: ['Invoice'],
    }),
  }),
});

export const { usePayInvoiceMutation, useGetStudentInvoicesQuery, useGetOutstandingInvoicesQuery } = feeApi;
