export interface StudentRequest {
  userId: string;
  gradeLevel: string;
  dateOfBirth?: string;
  parentId?: string;
}

export interface StudentResponse {
  id: string;
  userId: string;
  studentCode: string;
  dateOfBirth?: string;
  parentId?: string;
  gradeLevel: string;
  status: string;
  enrollmentDate: string;
}

export interface EnrollmentRequest {
  courseId: string;
}

export interface EnrollmentResponse {
  id: string;
  studentId: string;
  courseId: string;
  status: string;
  enrolledAt: string;
}
