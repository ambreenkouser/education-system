export type AttendanceStatus = 'PRESENT' | 'ABSENT' | 'LATE';

export interface AttendanceRequest {
  studentId: string;
  courseId: string;
  attendanceDate: string;
  status: AttendanceStatus;
  remarks?: string;
}

export interface AttendanceResponse {
  id: string;
  studentId: string;
  courseId: string;
  attendanceDate: string;
  status: AttendanceStatus;
  markedBy: string;
  remarks?: string;
  createdAt: string;
}

export interface AttendanceSummary {
  studentId: string;
  courseId: string;
  totalClasses: number;
  presentCount: number;
  absentCount: number;
  lateCount: number;
  attendancePercentage: number;
}
