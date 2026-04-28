export type InvoiceStatus = 'PENDING' | 'PAID' | 'OVERDUE';

export interface PaymentRequest {
  invoiceId: string;
  paidAmount: number;
  paymentMethod: string;
  transactionId?: string;
  remarks?: string;
}

export interface PaymentResponse {
  id: string;
  invoiceId: string;
  studentId: string;
  paidAmount: number;
  paymentMethod: string;
  transactionId?: string;
  paidAt: string;
}

export interface InvoiceResponse {
  id: string;
  studentId: string;
  feeStructureId: string;
  feeType: string;
  amount: number;
  dueDate: string;
  status: InvoiceStatus;
  createdAt: string;
}
