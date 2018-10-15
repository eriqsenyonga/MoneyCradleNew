package com.sentayzo.app;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.i2p.android.ext.floatingactionbutton.FloatingActionButton;
import net.i2p.android.ext.floatingactionbutton.FloatingActionsMenu;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 */
public class AccountsList extends Fragment {

    RecyclerView rvAccounts;
    FloatingActionsMenu fam;
    FloatingActionButton fabNewAccount;
    FloatingActionButton fabNewTrn;
    FloatingActionButton fabNewTransfer;
    FloatingActionButton fab;
    String tag = "AccountsList Fragment";
    TextView tv_totalAmount, tv_total_label, tvEmptyTitle, tvEmptyDescription;
    DbClass mDbClass;
    View rootView;
    String proceed;
    ConversionClass mCC;
    LinearLayout emptyState;
    ImageView ivEmptyImage;

    SharedPreferences billingPrefs;
    Animation animScaleUp, animScaleDown;
    AccountsRecyclerAdapter myAdapter;
    Typeface productSansRegular;
    Typeface productSansBold;

    public AccountsList() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        rootView = inflater.inflate(R.layout.fragment_accounts_list, container,
                false);

        tv_totalAmount = (TextView) rootView.findViewById(R.id.tv_totalView);
        tv_total_label = (TextView) rootView.findViewById(R.id.tv_total_label);
        rvAccounts = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        emptyState = rootView.findViewById(R.id.linlay_empty_state);
        ivEmptyImage = rootView.findViewById(R.id.iv_empty_state);
        tvEmptyTitle = (TextView) rootView.findViewById(R.id.tv_empty_title);
        tvEmptyDescription = (TextView) rootView.findViewById(R.id.tv_empty_description);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

        mCC = new ConversionClass(getActivity());

        productSansBold = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Product-Sans-Bold.ttf");
        productSansRegular = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Product-Sans-Regular.ttf");

        tv_total_label.setTypeface(productSansBold);
        tv_totalAmount.setTypeface(productSansRegular);


        mDbClass = new DbClass(getActivity());
        proceed = getResources().getString(R.string.proceed);


        fam = (FloatingActionsMenu) getActivity().findViewById(R.id.fam_fab);
        fabNewAccount = (FloatingActionButton) getActivity().findViewById(R.id.fab_newAccount);
        fabNewTrn = (FloatingActionButton) getActivity().findViewById(R.id.fab_newTransaction);
        fabNewTransfer = (FloatingActionButton) getActivity().findViewById(R.id.fab_newTransfer);
        fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);


        //   fab.setVisibility(View.GONE);

        if (fam.getVisibility() == View.GONE) {

            fam.setVisibility(View.VISIBLE);
            fam.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.scale_up));
        }

        //   TapTargetView.showFor(getActivity(), TapTarget.forView(fabNewTrn, "Quick Start", "Begin by creating account by tapping here"));

        billingPrefs = getActivity().getSharedPreferences("my_billing_prefs", 0);

        setUpEmptyState(R.drawable.ic_accounts, getString(R.string.empty_accounts_title), getString(R.string.empty_accounts_description));

        animScaleDown = AnimationUtils.loadAnimation(getActivity(), R.anim.scale_down);
        animScaleUp = AnimationUtils.loadAnimation(getActivity(), R.anim.scale_up);


        // mCursor = mDbClass.getAllOpenAccounts();

        Cursor accountsCursor = getActivity().getContentResolver().query(Uri.parse("content://"
                        + "SentayzoDbAuthority" + "/accounts"), null, null, null,
                null);

        if (accountsCursor.getCount() > 0) {
            emptyState.setVisibility(View.GONE);
        } else {
            emptyState.setVisibility(View.VISIBLE);
        }


        myAdapter = new AccountsRecyclerAdapter(accountsCursor, new CustomItemClickListener() {
            @Override
            public void onItemClick(View v, int position, final long accountId, boolean isLongClick) {
                //  txListInteraction.start(id, myAdapter);

                if (isLongClick) {

                    if (fam.isExpanded()) fam.collapse();
                    final String[] dialogList = getResources().getStringArray(
                            R.array.accountsListDialog);

                    AlertDialog.Builder builder = new AlertDialog.Builder(
                            getActivity());

                    builder.setItems(dialogList,
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int position) {


                                    //     Log.d("in alertdialog", "here");

                                    if (position == 0) {
                                        // if "View Info" is clicked --show dialog
                                        // with the account details

                                        AlertDialog.Builder builder3 = new AlertDialog.Builder(
                                                getActivity());
                                        builder3.setTitle(R.string.account_detailss);

                                        LayoutInflater inflater = getActivity()
                                                .getLayoutInflater();

                                        View view = inflater.inflate(
                                                R.layout.account_view, null);

                                        TextView tvDate = (TextView) view
                                                .findViewById(R.id.tv_date);
                                        TextView tvAcc = (TextView) view
                                                .findViewById(R.id.tv_accName);
                                        TextView tvType = (TextView) view
                                                .findViewById(R.id.tv_accType);
                                        TextView tvOpBal = (TextView) view
                                                .findViewById(R.id.tv_accOA);
                                        TextView tvNote = (TextView) view
                                                .findViewById(R.id.tv_accNote);
                                        TextView tvBal = (TextView) view
                                                .findViewById(R.id.tv_accBal);

                                        DbClass nDbClass = new DbClass(
                                                getActivity());

                                        nDbClass.open();

                                        Bundle infoBundle = nDbClass
                                                .getTheInfoOfAccountWithId(accountId);

                                        Long curBal = nDbClass
                                                .getCurrentBalance(accountId);

                                        nDbClass.close();

                                        tvDate.setText(mCC
                                                .dateForDisplay(infoBundle
                                                        .getString("acDate")));
                                        tvAcc.setText(infoBundle
                                                .getString("acName"));
                                        tvNote.setText(infoBundle
                                                .getString("acNote"));

                                        String opBal = mCC
                                                .valueConverter(infoBundle
                                                        .getLong("acOpenAmount"));
                                        String currBal = mCC.valueConverter(curBal);

                                        tvOpBal.setText(opBal);
                                        tvBal.setText(currBal);

                                        // values 1,2,3,4 below represent the _id of
                                        // the account type table in the Database
                                        if (infoBundle.getInt("acType") == 1) {
                                            tvType.setText(R.string.ac_type_cash);
                                        }
                                        if (infoBundle.getInt("acType") == 2) {
                                            tvType.setText(R.string.ac_type_bank);
                                        }
                                        if (infoBundle.getInt("acType") == 3) {
                                            tvType.setText(R.string.ac_type_asset);
                                        }
                                        if (infoBundle.getInt("acType") == 4) {
                                            tvType.setText(R.string.ac_type_liability);
                                        }

                                        builder3.setView(view);
                                        builder3.setPositiveButton(
                                                getResources().getString(
                                                        R.string.edit),
                                                new DialogInterface.OnClickListener() {

                                                    @Override
                                                    public void onClick(
                                                            DialogInterface dialog,
                                                            int which) {

                                                        // method stub
                                                        if (getAccountType(accountId) == 5) {
                                                            //if the accountType is Credit Card
                                                            DbClass myDbClass = new DbClass(
                                                                    getActivity());
                                                            myDbClass.open();
                                                            Bundle bundle = myDbClass
                                                                    .getCreditAccountInfoWithId(accountId);
                                                            myDbClass.close();
                                                            bundle.putLong("acId", accountId);

                                                            Intent i = new Intent(getActivity(),
                                                                    NewAccount.class);
                                                            i.putExtra("infoBundle", bundle);
                                                            startActivity(i);


                                                        } else {
                                                            //if the accountType is NOT CREDIT CARD

                                                            DbClass myDbClass = new DbClass(
                                                                    getActivity());
                                                            myDbClass.open();
                                                            Bundle bundle = myDbClass
                                                                    .getTheInfoOfAccountWithId(accountId);
                                                            myDbClass.close();
                                                            bundle.putLong("acId",
                                                                    accountId);

                                                            Intent i = new Intent(
                                                                    getActivity(),
                                                                    NewAccount.class);
                                                            i.putExtra("infoBundle",
                                                                    bundle);
                                                            startActivity(i);
                                                        }

                                                    }
                                                });
                                        builder3.setNeutralButton(
                                                getResources().getString(
                                                        R.string.ok),
                                                new DialogInterface.OnClickListener() {

                                                    @Override
                                                    public void onClick(
                                                            DialogInterface arg0,
                                                            int arg1) {

                                                    }
                                                });

                                        Dialog acViewDialog = builder3.create();

                                        acViewDialog.show();

                                    }

                                    if (position == 1) {
                                        // if "New Transaction" is clicked
                                        Intent i = new Intent(getActivity(),
                                                NewTransaction.class);
                                        i.putExtra("accountId", accountId);
                                        startActivity(i);


                                    }

                                    if (position == 3) {
                                        // if "Account History" is clicked
                                        Intent i = new Intent(getActivity(),
                                                OverviewActivity.class);
                                        i.putExtra("Id", accountId);
                                        i.putExtra("whichOverview", OverviewActivity.KEY_ACCOUNT_OVERVIEW);
                                        startActivity(i);

                                    }

                                    if (position == 4) {
                                        // if "Edit" is clicked

                                        if (getAccountType(accountId) == 5) {
                                            //if the accountType is Credit Card
                                            DbClass myDbClass = new DbClass(
                                                    getActivity());
                                            myDbClass.open();
                                            Bundle bundle = myDbClass
                                                    .getCreditAccountInfoWithId(accountId);
                                            myDbClass.close();
                                            bundle.putLong("acId", accountId);

                                            Intent i = new Intent(getActivity(),
                                                    NewAccount.class);
                                            i.putExtra("infoBundle", bundle);
                                            startActivity(i);


                                        } else {
                                            //if the accountType is NOT CREDIT CARD
                                            DbClass myDbClass = new DbClass(
                                                    getActivity());
                                            myDbClass.open();
                                            Bundle bundle = myDbClass
                                                    .getTheInfoOfAccountWithId(accountId);
                                            myDbClass.close();
                                            bundle.putLong("acId", accountId);

                                            Intent i = new Intent(getActivity(),
                                                    NewAccount.class);
                                            i.putExtra("infoBundle", bundle);
                                            startActivity(i);
                                        }

                                    }

                                    if (position == 5) {
                                        // if close account is clicked
                                        String closeDialogTitle = getResources()
                                                .getString(
                                                        R.string.closeDialogTitle);
                                        String closeDialogMessage = getResources()
                                                .getString(
                                                        R.string.closeDialogMessage);
                                        AlertDialog.Builder closeBuilder = new AlertDialog.Builder(
                                                getActivity());
                                        closeBuilder.setTitle(closeDialogTitle);
                                        closeBuilder.setMessage(closeDialogMessage
                                                + " \n\n" + proceed);

                                        closeBuilder
                                                .setNegativeButton(
                                                        getResources().getString(
                                                                R.string.no),
                                                        new DialogInterface.OnClickListener() {

                                                            @Override
                                                            public void onClick(
                                                                    DialogInterface arg0,
                                                                    int arg1) {


                                                            }
                                                        });

                                        closeBuilder
                                                .setPositiveButton(
                                                        getResources().getString(
                                                                R.string.yes),
                                                        new DialogInterface.OnClickListener() {

                                                            @Override
                                                            public void onClick(
                                                                    DialogInterface arg0,
                                                                    int arg1) {


                                                                DbClass mDbClass = new DbClass(
                                                                        getActivity());

                                                                mDbClass.open();

                                                                mDbClass.closeAccountWithId(accountId);

                                                                mDbClass.close();

                                                                // myAdapter.swapCursor(mDbClass.getAllOpenAccounts());

                                                                Cursor accountsCursor = getActivity().getContentResolver().query(Uri.parse("content://"
                                                                                + "SentayzoDbAuthority" + "/accounts"), null, null, null,
                                                                        null);

                                                                if (accountsCursor.getCount() > 0) {
                                                                    emptyState.setVisibility(View.GONE);
                                                                } else {
                                                                    emptyState.setVisibility(View.VISIBLE);
                                                                }


                                                                myAdapter.changeCursor(accountsCursor);

                                                                myAdapter.notifyDataSetChanged();
                                                            }
                                                        });

                                        Dialog closeDialog = closeBuilder.create();
                                        closeDialog.show();

                                    }

                                    if (position == 6) {
                                        // if "Delete" is clicked
                                        String deleteDialogTitle = getResources()
                                                .getString(
                                                        R.string.deleteDialogTitle);
                                        String deleteDialogMessage = getResources()
                                                .getString(
                                                        R.string.deleteDialogMessage);

                                        AlertDialog.Builder builder2 = new AlertDialog.Builder(
                                                getActivity());
                                        builder2.setTitle(deleteDialogTitle);
                                        builder2.setMessage(deleteDialogMessage
                                                + " \n\n" + proceed);
                                        builder2.setNegativeButton(
                                                getResources().getString(
                                                        R.string.no),
                                                new DialogInterface.OnClickListener() {

                                                    @Override
                                                    public void onClick(
                                                            DialogInterface arg0,
                                                            int arg1) {

                                                        // method stub

                                                    }
                                                });

                                        builder2.setPositiveButton(
                                                getResources().getString(
                                                        R.string.yes),
                                                new DialogInterface.OnClickListener() {

                                                    @Override
                                                    public void onClick(
                                                            DialogInterface arg0,
                                                            int arg1) {


                                                        DbClass aDbClass = new DbClass(
                                                                getActivity());
                                                        aDbClass.open();
                                                        aDbClass.deleteAccountWithId(accountId);
                                                        aDbClass.close();

                                                        getTotal();

                                                        Cursor accountsCursor = getActivity().getContentResolver().query(Uri.parse("content://"
                                                                        + "SentayzoDbAuthority" + "/accounts"), null, null, null,
                                                                null);

                                                        if (accountsCursor.getCount() > 0) {
                                                            emptyState.setVisibility(View.GONE);
                                                        } else {
                                                            emptyState.setVisibility(View.VISIBLE);
                                                        }


                                                        myAdapter.changeCursor(accountsCursor);

                                                        myAdapter.notifyDataSetChanged();

//                                                    Snackbar.make(rootView.findViewById(R.id.coordinator), "Account deleted", Snackbar.LENGTH_SHORT).show();
                                                    }
                                                });

                                        Dialog delDialog = builder2.create();
                                        delDialog.show();

                                    }

                                    if (position == 2) {
                                        // if "New Transfer" is clicked

                                        Intent i = new Intent(getActivity(),
                                                NewTransfer.class);
                                        i.putExtra("accountId", accountId);
                                        startActivity(i);

                                    }

                                }
                            });

                    Dialog d = builder.create();

                    d.show();
                } else {


                    Intent i = new Intent(getActivity(),
                            OverviewActivity.class);
                    i.putExtra("Id", accountId);
                    i.putExtra("whichOverview", OverviewActivity.KEY_ACCOUNT_OVERVIEW);
                    startActivity(i);


                }
            }
        });

        rvAccounts.setAdapter(myAdapter);

        rvAccounts.setLayoutManager(new LinearLayoutManager(getActivity()));


        // procedure for the total
        getTotal();

        rvAccounts.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (dy > 0) {

                    if (fam.getVisibility() == View.GONE) {
                    } else {
                        fam.collapse();
                        fam.startAnimation(animScaleDown);
                        fam.setVisibility(View.GONE);
                    }
                } else if (dy < 0) {

                    if (fam.getVisibility() == View.VISIBLE) {
                    } else {
                        fam.setVisibility(View.VISIBLE);
                        fam.startAnimation(animScaleUp);
                    }
                }
            }
        });


        fabNewAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SkusAndBillingThings skusAndBillingThings = new SkusAndBillingThings(getActivity());

                Boolean premium = skusAndBillingThings.isPremiumUser();

                Boolean access = skusAndBillingThings.hasAccess();


                int numberOfAccounts = new DbClass(getActivity())
                        .getNumberOfAccounts();

                if (numberOfAccounts < 2) {
                    Intent i = new Intent(getActivity(), NewAccount.class);
                    startActivity(i);

                } else {

                    if (premium && access) {
                        Intent i = new Intent(getActivity(), NewAccount.class);
                        startActivity(i);

                    } else if (premium && access == false) {
                        //account hold so show dialog for account hold

                        skusAndBillingThings.showAccountHoldDialog();


                    } else if (!premium) {

                        showPaymentDialog(getActivity());


                    }

                }


            }
        });


        fabNewTrn.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {
                DbClass mDbClass = new DbClass(getActivity());
                mDbClass.open();
                Boolean accountsAvailable = mDbClass.checkForAccounts();
                mDbClass.close();

                if (accountsAvailable == false) {

                    View dv = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_alert, null);
                    CardView button = dv.findViewById(R.id.b_alert_button);
                    TextView tvTitle = dv.findViewById(R.id.tv_alert_title);
                    TextView tvMessage = dv.findViewById(R.id.tv_alert_message);
                    TextView tvButtonText = dv.findViewById(R.id.tv_alert_button);

                    tvTitle.setText(getString(R.string.title_activity_new_transaction));
                    tvMessage.setText(getString(R.string.createAccountFirst));
                    tvButtonText.setText(getString(R.string.add_new_account));

                    String createAccountFirst = getResources().getString(
                            R.string.createAccountFirst);

                    AlertDialog.Builder builder = new AlertDialog.Builder(
                            getActivity());

                    builder.setView(dv);

                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Intent i = new Intent(getActivity(), NewAccount.class);
                            startActivity(i);

                        }
                    });
               /*     builder.setMessage(createAccountFirst);
                    builder.setNeutralButton(getResources().getString(R.string.ok),
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {


                                }
                            });
*/
                    Dialog d = builder.create();
                    d.show();

                } else {
                    Intent i = new Intent(getActivity(), NewTransaction.class);
                    startActivity(i);
                }
            }
        });

        fabNewTransfer.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {
                int numberOfAccounts = new DbClass(getActivity())
                        .getNumberOfAccounts();


                fam.collapse();

                if (numberOfAccounts < 2) {

                    // String msg = getResources().getString(R.string.accounts_two);

                    View dv = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_alert, null);
                    CardView button = dv.findViewById(R.id.b_alert_button);
                    TextView tvTitle = dv.findViewById(R.id.tv_alert_title);
                    TextView tvMessage = dv.findViewById(R.id.tv_alert_message);
                    TextView tvButtonText = dv.findViewById(R.id.tv_alert_button);

                    tvTitle.setText(getString(R.string.title_activity_new_transfer));
                    tvMessage.setText(getString(R.string.atleast_two_accounts));
                    tvButtonText.setText(getString(R.string.add_new_account));


                    AlertDialog.Builder builder = new AlertDialog.Builder(
                            getActivity());

                    builder.setView(dv);

                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Intent i = new Intent(getActivity(), NewAccount.class);
                            startActivity(i);

                        }
                    });

                   /* AlertDialog.Builder builder = new AlertDialog.Builder(
                            getActivity());
                    builder.setMessage(R.string.atleast_two_accounts);

                    builder.setNeutralButton(getResources().getString(R.string.ok),
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {


                                }
                            });
*/
                    Dialog d = builder.create();
                    d.show();
                } else {

                    Intent i = new Intent(getActivity(), NewTransfer.class);
                    startActivity(i);
                }
            }
        });


    }

    private void setUpEmptyState(int emptyImage, String emptyTitle, String emptyDescription) {

        ivEmptyImage.setImageResource(emptyImage);
        tvEmptyTitle.setText(emptyTitle);
        tvEmptyDescription.setText(emptyDescription);


    }

    private void getTotal() {


        Long totalAmount = mDbClass.totalTransactionAmount();

        ConversionClass mCC = new ConversionClass(getActivity());

        String totAmt = mCC.valueConverter(totalAmount);

        if (totalAmount < 0) {

            tv_totalAmount.setTextColor(Color.RED);
        }

        if (totalAmount >= 0) {

            tv_totalAmount.setTextColor(Color.parseColor("#08ad14"));
        }

        try {
            tv_totalAmount.setText(totAmt);
        } catch (Exception e) {

            Log.d(tag, e.toString());
        }
    }


    public long getAccountType(long accountId) {

        DbClass dbc = new DbClass(getActivity());

        dbc.open();
        long accountTypeId = dbc.getAccountTypeId(accountId);
        dbc.close();


        return accountTypeId;
    }

    private void showPaymentDialog(final Context context) {

        SkusAndBillingThings skusAndBillingThings = new SkusAndBillingThings(context);
        skusAndBillingThings.showPaymentDialog(getString(R.string.upgrade_unlimited_accounts));


    /*    AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setMessage(context.getResources().getString(
                R.string.payment_dialog_message)
                + "\n\n"
                + context.getResources()
                .getString(R.string.unlock_all_features) + " ?");

        builder.setNegativeButton(
                context.getResources().getString(R.string.no),
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                    }
                });

        builder.setPositiveButton(context.getResources()
                        .getString(R.string.yes),
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                        Intent i = new Intent(context, UpgradeActivity.class);
                        startActivity(i);
                    }
                });

        Dialog paymentDialog = builder.create();
        paymentDialog.show();*/

    }
}
