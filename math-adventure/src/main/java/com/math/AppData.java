package com.math;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class AppData {
    private static String db_url = "jdbc:mysql://localhost/mathadventure";
    private static String dbuser = "root";
    private static String pass = "makan";

    public static Connection connect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(db_url,dbuser,pass);
        } catch (SQLException e) {
            // System.out.println(e.getMessage());
            System.out.println("tidak dapat terhubung dengan database\n cek user dan password mysql");
        }
        return conn;
    }

    public static User LoginUser(String username, String password){
        Connection conn = connect();
        User user = null;
        boolean admin = false;
        String sql = "SELECT * FROM USER WHERE Username = ? AND PASSWORD = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                System.out.println(rs.getString("Role"));
                admin = rs.getString("Role").equals("Admin");
                user = new User(rs.getString("Username"), admin);
            }
            
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if(conn != null)
                    conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return user;
    }
    public static void getSoalList(List<Question> questions){
        questions.removeAll(questions);
        Connection conn = connect();
        try {
            var stmt = conn.createStatement();
            String sql= "SELECT * FROM Soal_Matematika";
            var result = stmt.executeQuery(sql);
            Question question = null;
            while (result.next()) {
                int type = result.getInt("tipe_soal");
                if(type ==1 ){
                    question = new Question(result.getInt("id"),result.getString("pertanyaan"),result.getString("pilihan_a"),result.getString("pilihan_b"),result.getString("pilihan_c"),result.getString("jawaban_benar_pg"));
                }else if(type == 2){
                    question = new Question(result.getInt("id"),result.getString("pertanyaan"), result.getString("jawaban_benar_essay"));
                }
                questions.add(question);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            System.out.println("failed to get data");
        }finally {
            try {
                if(conn != null)
                    conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    public static void getRandomQuestion(List<Question> questions){
        questions.removeAll(questions);
        Connection conn = connect();
        try {
            var stmt = conn.createStatement();
            String sql= "SELECT * FROM Soal_Matematika  ORDER BY RAND()";
            var result = stmt.executeQuery(sql);
            Question question = null;
            while (result.next()) {
                int type = result.getInt("tipe_soal");
                if(type ==1 ){
                    question = new Question(result.getInt("id"),result.getString("pertanyaan"),result.getString("pilihan_a"),result.getString("pilihan_b"),result.getString("pilihan_c"),result.getString("jawaban_benar_pg"));
                }else if(type == 2){
                    question = new Question(result.getInt("id"),result.getString("pertanyaan"), result.getString("jawaban_benar_essay"));
                }
                questions.add(question);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            System.out.println("failed to get data");
        }finally {
            try {
                if(conn != null)
                    conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    public static boolean insertQuestion(Question question){
        boolean success = false;
        Connection conn = connect();
        try {
            if(question.getQuestionType()==1){
                String sql = "INSERT INTO Soal_Matematika (tipe_soal,pertanyaan,pilihan_a,pilihan_b,pilihan_c,jawaban_benar_pg) VALUES (?,?,?,?,?,?)";
                PreparedStatement psmt = conn.prepareStatement(sql);
                psmt.setInt(1,question.getQuestionType());
                psmt.setString(2, question.getQuestionString());
                psmt.setString(3, question.getOptionA());
                psmt.setString(4, question.getOptionB());
                psmt.setString(5, question.getOptionC());
                psmt.setString(6, question.getAnswerPg());
                int rowsInserted = psmt.executeUpdate();
                if (rowsInserted > 0)
                    System.out.println("A new question was inserted successfully!");
                success = true;
            }else if(question.getQuestionType()==2){
                String sql = "INSERT INTO Soal_Matematika (tipe_soal,pertanyaan,jawaban_benar_essay) VALUES (?,?,?)";
                PreparedStatement psmt = conn.prepareStatement(sql);
                psmt.setInt(1,question.getQuestionType());
                psmt.setString(2, question.getQuestionString());
                psmt.setString(3, question.getAnswerEssay());
                int rowsInserted = psmt.executeUpdate();
                if(rowsInserted>0)
                    System.out.println("A new question was inserted successfully!");
                success = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return success;
    }
}
