# PaySlip Pro 📱💼

**PaySlip Pro** is a modern Android application designed for factory, operations, and corporate employees to calculate their exact salary slip, view itemized statutory deductions (PF, ESI, Canteen, Shift allowances, Overtime), generate professional PDF salary statements, and plan their monthly spendings.

---

## ✨ Features

1. **Persistent Device Session**
   - Register with Name, Employee ID, and Password.
   - Session stays active across app restarts until the user explicitly logs out.

2. **Automated Salary & Deduction Engine**
   - **Basic Salary**: Base pay (without OT)
   - **HRA Calculation**: Pro-rated HRA = `((max HRA / total days in month) * present days)`
   - **Attendance**: Total present days tracked against total month days
   - **Shift Allowances**: 1st shift (₹35/day) & 2nd shift (₹45/day)
   - **Overtime (OT) Pay**: Hourly wage computed as `(((Basic + HRA) / 30) / 8)`, doubled for OT rate `× 2 × OT Hours`
   - **Weekly Attendance Bonus**: Bonus awarded for each full week without leaves
   - **Special Bonus**: Customizable addition
   - **Statutory Deductions**:
     - **Provident Fund (PF)**: `12% of Basic Salary`
     - **Employee State Insurance (ESI)**: `0.75% of Gross Earned Salary`
     - **Canteen Charges**: `Present Days × ₹9/day`
     - **Other Deductions**: Customizable deductions
   - **Net Payable Salary**: `Gross - Total Deductions`

3. **PDF Generation & Export**
   - Built-in PDF generator renders official payslip documents with company header, employee metadata, itemized earnings & deductions tables, net pay in numbers and words, and signature boxes.
   - Download directly to device storage and share via WhatsApp, Email, or Print.

4. **Spending & Budget Planner**
   - 50/30/20 Budgeting Rule Preset (50% Needs, 30% Wants, 20% Savings).
   - Custom budget allocation for House Rent, Food & Groceries, Utilities, EMIs, Leisure, and Investments.
   - Live visual budget health meter and savings tracker.

5. **Monthly History & Archives**
   - Room Database persists every generated statement locally.
   - Review past months and re-download PDFs anytime.

---

## 🚀 How to Build the APK on GitHub

We have configured a ready-to-run **GitHub Actions Workflow** in `.github/workflows/build-apk.yml`.

### Step-by-Step:
1. **Push your code to GitHub**:
   ```bash
   git init
   git add .
   git commit -m "Initial commit of PaySlip Pro"
   git branch -M main
   git remote add origin https://github.com/YOUR_USERNAME/YOUR_REPOSITORY.git
   git push -u origin main
   ```

2. **GitHub Actions will automatically run**:
   - Navigate to the **Actions** tab in your GitHub repository.
   - You will see the **Build Android APK** workflow running.

3. **Download your APK**:
   - Once the build succeeds, click on the workflow run.
   - Scroll down to the **Artifacts** section at the bottom.
   - Click on **PaySlipPro-Debug-APK** to download your ready-to-install `.apk` file!
   - Transfer to your Android phone and install.
