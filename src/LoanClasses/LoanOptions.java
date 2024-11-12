package LoanClasses;

public class LoanOptions {
    private int loan_option_id;
    private String loan_option_type;
    private double interest_rate;
    private int loan_duration_year;
    private double max_loan_amount;
    private double min_loan_amount;
    
    private static double personal_loan_rate = 0.07; //7% for Personal Loans
    private static double auto_loan_rate = 0.04;     //4% for Auto Loans
    private static double mortgage_loan_rate = 0.05; //5% for Mortgages

    public LoanOptions(int loan_option_id){

        if (loan_option_id == 1){
            this.loan_duration_year = loan_option_id;
            this.loan_option_type = "Personal Loan";
            this.interest_rate = personal_loan_rate;     
        } else if (loan_option_id == 2) {
            this.loan_duration_year = loan_option_id;
            this.loan_option_type = "Auto Loan";
            this.interest_rate = auto_loan_rate;     
        } else if (loan_option_id == 3){
            this.loan_duration_year = loan_option_id;
            this.loan_option_type = "Mortgage";
            this.interest_rate = mortgage_loan_rate;    
        } else {
            throw new IllegalArgumentException("Invalid loan option ID: " + loan_option_id);       
        }
    }

    public int getLoanOptionId() {
        return loan_option_id;
    }

    public void setLoanOptionId(int loan_option_id) {
        this.loan_option_id = loan_option_id;
    }

    public String getLoanOptionType() {
        return loan_option_type;
    }

    public void setLoanOptionType(String loan_option_type) {
        this.loan_option_type = loan_option_type;
    }

    public double getInterestRate() {
        return interest_rate;
    }

    public void setInterestRate(double interest_rate) {
        this.interest_rate = interest_rate;
    }

    public int getLoanDurationYear() {
        return loan_duration_year;
    }

    public void setLoanDurationYear(int loan_duration_year) {
        this.loan_duration_year = loan_duration_year;
    }

    public double getMaxLoanAmount() {
        return max_loan_amount;
    }

    public void setMaxLoanAmount(double max_loan_amount) {
        this.max_loan_amount = max_loan_amount;
    }

    public double getMinLoanAmount() {
        return min_loan_amount;
    }

    public void setMinLoanAmount(double min_loan_amount) {
        this.min_loan_amount = min_loan_amount;
    }

}
