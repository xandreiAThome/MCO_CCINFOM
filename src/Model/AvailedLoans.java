package Model;

import DateClass.Date;

public class AvailedLoans {

    enum LoanStatus {
        ACTIVE,
        CLOSED;
    }
    
    private int loan_id;
    private int loan_option_id;
    private double principle_amount;
    private double first_month_principal_amortization;
    private double succeding_principal_amortization;
    private double interest_amortization;
    private double prinicple_balance;
    private double interest_balance;
    private Date start_date;
    private Date end_date;
    private Date month_payment_day;
    private LoanStatus loan_status;
    private int customer_id;

    public AvailedLoans(){

    }
}
