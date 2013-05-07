CREATE TABLE [meeting] (
  [id] INTEGER PRIMARY KEY,
  [user_id] INTEGER,
  [mtime] TEXT,
  [address] TEXT,
  [join_users] TEXT,
  [title] TEXT,
  [content] TEXT,
  [send_user] TEXT,
  [require] TEXT,
  [receive_time] LONG,
  [meeting_type] TEXT,
  [read_flag] INTEGER
  );
  
CREATE TABLE [warning] (
  [id] INTEGER PRIMARY KEY,
  [user_id] INTEGER,
  [title] TEXT,
  [content] TEXT,
  [send_user] TEXT,
  [receive_time] LONG,
  [solve_users] TEXT,
  [explain] TEXT,
  [mtime] TEXT,
  [lb] TEXT,
  [read_flag] INTEGER
);