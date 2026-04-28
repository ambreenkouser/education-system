export type ExamType = 'MIDTERM' | 'FINAL' | 'QUIZ' | 'ASSIGNMENT';

export interface ExamRequest {
  courseId: string;
  title: string;
  examDate: string;
  totalMarks: number;
  type: ExamType;
}

export interface Exam {
  id: string;
  courseId: string;
  title: string;
  examDate: string;
  totalMarks: number;
  type: ExamType;
  createdAt: string;
}

export interface GradeRequest {
  studentId: string;
  examId: string;
  marksObtained: number;
}

export interface GradeResponse {
  id: string;
  studentId: string;
  examId: string;
  examTitle: string;
  marksObtained: number;
  totalMarks: number;
  gradeLetter: string;
  gradePoints: number;
  gradedAt: string;
}
