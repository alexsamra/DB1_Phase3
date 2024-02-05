import java.sql.*;
import java.util.Scanner;

public class Reporting {
    public static <Statement> void main(String[] args) throws SQLException {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return;
        }
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(
                    "jdbc:oracle:thin:@oracle.wpi.edu:1521:orcl",
                    args[0],
                    args[1]);
        } catch (SQLException e) {
            System.out.println("Connection Failed! Check output console");
            e.printStackTrace();
            return;
        }

        Scanner scanner = new Scanner(System.in);

        if (args.length == 2) {
            System.out.println("1- Report Patients Basic Information");
            System.out.println("2- Report Doctors Basic Information");
            System.out.println("3- Report Admissions Information");
            System.out.println("4- Update Admissions Payment");
            return;
        } else if (args[2].equals("1")) {
            System.out.print("Enter Patient SSN: ");
            String ssn = scanner.nextLine();
            try {
                PreparedStatement pstmt = connection.prepareStatement("SELECT * FROM PATIENT WHERE SSN = ?");
                pstmt.setString(1, ssn);
                ResultSet resultSet = pstmt.executeQuery();

                // Check if there is a result
                if (resultSet.next()) {
                    // Retrieve patient information from the result set
                    String patientFName = resultSet.getString("FIRSTNAME");
                    String patientLName = resultSet.getString("LASTNAME");
                    String patientAddy = resultSet.getString("ADDRESS");

                    System.out.println("Patient SSN: " + ssn);
                    System.out.println("Patient First Name: " + patientFName);
                    System.out.println("Patient Last Name: " + patientLName);
                    System.out.println("Patient Address: " + patientAddy);

                } else {
                    System.out.println("Patient with SSN " + ssn + " not found.");
                }

                resultSet.close();
                pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else if (args[2].equals("2")) {
            System.out.print("Enter Doctor ID: ");
            int docID = scanner.nextInt();
            try {
                PreparedStatement pstmt = connection.prepareStatement("SELECT * FROM DOCTOR WHERE EMPLOYEEID = ?");
                pstmt.setInt(1, docID);
                ResultSet resultSet = pstmt.executeQuery();

                PreparedStatement pstmt2 = connection.prepareStatement("SELECT * FROM EMPLOYEE WHERE ID = ?");
                pstmt2.setInt(1, docID);
                ResultSet resultSet2 = pstmt2.executeQuery();

                if (resultSet.next() && resultSet2.next()) {
                    String gender = resultSet.getString("GENDER");
                    String school = resultSet.getString("GRADUATEDFROM");
                    String special = resultSet.getString("SPECIALTY");

                    String fName = resultSet2.getString("FNAME");
                    String lName = resultSet2.getString("LNAME");


                    System.out.println("Doctor ID: " + docID);
                    System.out.println("Doctor First Name: " + fName);
                    System.out.println("Doctor Last Name: " + lName);
                    System.out.println("Doctor Gender: " + gender);
                    System.out.println("Doctor Graduated From: " + school);
                    System.out.println("Doctor Specialty: " + special);

                } else {
                    System.out.println("Doctor with ID " + docID + " not found.");
                }

                resultSet.close();
                pstmt.close();
                resultSet2.close();
                pstmt2.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else if (args[2].equals("3")) {
            System.out.print("Enter Admission Number: ");
            String addNum = scanner.nextLine();
            try {
                PreparedStatement pstmt = connection.prepareStatement("SELECT * FROM ADMISSION WHERE NUM = ?");
                pstmt.setString(1, addNum);
                ResultSet resultSet = pstmt.executeQuery();

                PreparedStatement pstmt2 = connection.prepareStatement("SELECT * FROM STAYIN WHERE ADMISSIONNUM = ?");
                pstmt2.setString(1, addNum);
                ResultSet resultSet2 = pstmt2.executeQuery();

                PreparedStatement pstmt3 = connection.prepareStatement("SELECT * FROM EXAMINE WHERE ADMISSIONNUM = ?");
                pstmt3.setString(1, addNum);
                ResultSet resultSet3 = pstmt3.executeQuery();

                // Check if there is a result
                if (resultSet.next()) {
                    // Retrieve patient information from the result set
                    String startDate = resultSet.getString("ADMISSIONDATE");
                    String patientSSN = resultSet.getString("PATIENT_SSN");
                    String totalPayment = resultSet.getString("TOTALPAYMENT");

                    System.out.println("Admission Number: " + addNum);
                    System.out.println("Patient SSN: " + patientSSN);
                    System.out.println("Admission date (start date): " + startDate);
                    System.out.println("Total Payment: " + totalPayment);

                    System.out.println("Rooms:");
                    String roomNum, stDate, enDate;
                    while(resultSet2.next()){
                        roomNum = resultSet2.getString("ROOMNUM");
                        stDate = resultSet2.getString("STARTDATE");
                        enDate = resultSet2.getString("ENDDATE");
                        System.out.println("RoomNum: " + roomNum + " FromDate: " + stDate + " ToDate: " + enDate);
                    }

                    System.out.println("Doctors examined the patient in this admission:");
                    int docExID;
                    while(resultSet3.next()){
                        docExID = resultSet3.getInt("DOCTOR_ID");
                        System.out.println("Doctor ID: " + docExID);
                    }


                } else {
                    System.out.println("Admission with ID " + addNum + " not found.");
                }

                resultSet.close();
                pstmt.close();
                resultSet2.close();
                pstmt2.close();
                resultSet3.close();
                pstmt3.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else if (args[2].equals("4")) {
            System.out.print("Enter Admission Number: ");
            String addNum = scanner.nextLine();
            System.out.print("Enter the new total payment: ");
            int payment = scanner.nextInt();
            int oldPayment = 0;
            try {
                PreparedStatement pstmt = connection.prepareStatement("SELECT * FROM ADMISSION WHERE NUM = ?");
                pstmt.setString(1, addNum);
                ResultSet resultSet = pstmt.executeQuery();

                if (resultSet.next()) {
                    oldPayment = resultSet.getInt("TOTALPAYMENT");

                } else {
                    System.out.println("Admission with Num " + addNum + " not found.");
                }

                int newPayment = oldPayment - payment;
                PreparedStatement pstmt2 = connection.prepareStatement("UPDATE ADMISSION SET TOTALPAYMENT = ? WHERE NUM = ?");
                pstmt2.setInt(1, newPayment);
                pstmt2.setString(2, addNum);
                pstmt2.executeUpdate();

                resultSet.close();
                pstmt.close();
                pstmt2.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }


        connection.close();
    }
}


