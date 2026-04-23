#!/bin/bash
set -e

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" <<-EOSQL
  CREATE DATABASE edumanage_auth;
  CREATE DATABASE edumanage_user;
  CREATE DATABASE edumanage_student;
  CREATE DATABASE edumanage_course;
  CREATE DATABASE edumanage_timetable;
  CREATE DATABASE edumanage_attendance;
  CREATE DATABASE edumanage_grade;
  CREATE DATABASE edumanage_fee;
  CREATE DATABASE edumanage_report;
EOSQL
