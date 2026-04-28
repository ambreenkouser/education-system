export interface CourseRequest {
  code: string;
  name: string;
  description?: string;
  credits: number;
  teacherId?: string;
  maxStudents: number;
}

export interface CourseResponse {
  id: string;
  code: string;
  name: string;
  description?: string;
  credits: number;
  teacherId?: string;
  maxStudents: number;
  enrolledCount: number;
  status: string;
  hasCapacity: boolean;
  createdAt: string;
}

export interface EligibilityResponse {
  courseId: string;
  studentId: string;
  eligible: boolean;
  reason: string;
}
