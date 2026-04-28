export type DayOfWeek = 'MONDAY' | 'TUESDAY' | 'WEDNESDAY' | 'THURSDAY' | 'FRIDAY' | 'SATURDAY' | 'SUNDAY';

export interface TimeSlotRequest {
  courseId: string;
  teacherId: string;
  roomId: string;
  dayOfWeek: DayOfWeek;
  startTime: string;
  endTime: string;
}

export interface TimeSlotResponse {
  id: string;
  courseId: string;
  teacherId: string;
  roomId: string;
  roomName: string;
  dayOfWeek: DayOfWeek;
  startTime: string;
  endTime: string;
}

export interface RoomRequest {
  name: string;
  capacity: number;
  building?: string;
  floor?: string;
}

export interface Room {
  id: string;
  name: string;
  capacity: number;
  building?: string;
  floor?: string;
}
