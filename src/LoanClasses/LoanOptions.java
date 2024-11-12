package LoanClasses;

public class LoanOptions {
    private int loan_option_id;
    private String loan_option_type;
    private double interest_rate;
    private int loan_duration_year;
    private double max_loan_amount;
    private double min_loan_amount;

    public LoanOptions(int loan_option_id){

        if (loan_option_id == 1){
            this.loan_duration_year = loan_option_id;
            this.loan_option_type = "Personal Loan";
            this.interest_rate = 0.07;      //7% for Personal Loans
        } else if (loan_option_id == 2) {
            this.loan_duration_year = loan_option_id;
            this.loan_option_type = "Auto Loan";
            this.interest_rate = 0.04;     //4% for Auto Loans
        } else if (loan_option_id == 3){
            this.loan_duration_year = loan_option_id;
            this.loan_option_type = "Mortgage";
            this.interest_rate = 0.05;    //5% for Mortgages
        } else {
            throw new IllegalArgumentException("Invalid loan option ID: " + loan_option_id);        }
    }

}
