package com.za.toptitup.loginlibrary.retailerFragments.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.za.toptitup.loginlibrary.R
import com.za.toptitup.loginlibrary.databinding.FragmentDasboardHomeBinding
import com.za.toptitup.loginlibrary.retailerFragments.adapters.DashboardAdapter
import com.za.toptitup.loginlibrary.retailerFragments.RetailerDashboardActivity
import com.za.toptitup.loginlibrary.retailerFragments.models.DashboardModule

class DashboardHomeFragment : Fragment() {

    private var _binding: FragmentDasboardHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDasboardHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as? RetailerDashboardActivity)?.showBackButton(false)

        val modules = listOf(
            DashboardModule("Transfer to Wallet", R.drawable.wallet_transfer, 1),
            DashboardModule("Wholesaler Payment", R.drawable.ic_wholesaler_pay, 2),
            DashboardModule("Transfer to Stores", R.drawable.store_transfer, 3),
            DashboardModule("Transfer to Bank Accounts", R.drawable.transfer_to_bank, 4),
            DashboardModule("Pay Suppliers", R.drawable.prov_suppliers, 5),
            DashboardModule("Money Transfer", R.drawable.sendmoney, 6),
            DashboardModule("My Sales Transactions", R.drawable.daily_sales_report, 7),
            DashboardModule("View Commissions & Invoices", R.drawable.commision_statement_, 8),
            DashboardModule("Financial Transactions", R.drawable.banking, 9),
            DashboardModule("Sales & Shift Reports", R.drawable.reports, 10),
            DashboardModule("Deposit History", R.drawable.deposit_slip, 11),
            DashboardModule("Topitup Banking Details", R.drawable.ic_banking_detail, 12),
            DashboardModule("POS Users", R.drawable.users, 13),
            DashboardModule("Website Users", R.drawable.ic_refresh_users, 14),
            DashboardModule("My Devices", R.drawable.settings, 15),
            DashboardModule("View OTP", R.drawable.ic_otp, 16),
            DashboardModule("Topitup Gift Vouchers", R.drawable.ic_gift_voucher, 17),
            DashboardModule("Supplier Order", R.drawable.ic_supplier_order, 18),
        )

        binding.rvModules.layoutManager = GridLayoutManager(requireContext(), 3)
        binding.rvModules.adapter = DashboardAdapter(modules)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
