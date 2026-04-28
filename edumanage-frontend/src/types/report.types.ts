export interface StudentSummary {
  studentId: string;
  studentCode: string;
  totalEnrollments: number;
  lastUpdated: string;
}

export interface AttendanceReportSummary {
  id: string;
  studentId: string;
  courseId: string;
  totalClasses: number;
  presentCount: number;
  absentCount: number;
  lateCount: number;
  lastUpdated: string;
  attendancePercentage: number;
}

export interface GradeSummary {
  id: string;
  studentId: string;
  examId: string;
  examTitle: string;
  marksObtained: number;
  gradeLetter: string;
  gradePoints: number;
  gradedAt: string;
}

export interface FeeSummary {
  id: string;
  studentId: string;
  invoiceId: string;
  paidAmount: number;
  paymentMethod: string;
  transactionId?: string;
  paidAt: string;
}
