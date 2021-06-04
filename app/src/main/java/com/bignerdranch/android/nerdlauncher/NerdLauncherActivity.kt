package com.bignerdranch.android.nerdlauncher

import android.content.Intent
import android.content.pm.ResolveInfo
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toBitmap
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

private const val TAG = "NerdLauncherActivity"

class NerdLauncherActivity:AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nerd_launcher)

        recyclerView = findViewById(R.id.app_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)

        setupAdapter()

    }

    //page: 452
    private class ActivityHolder(itemView: View):
            RecyclerView.ViewHolder(itemView), View.OnClickListener{
            private val appIcon =  itemView.findViewById(android.R.id.icon) as ImageView
            private val nameTextView = itemView.findViewById(android.R.id.text1) as TextView
                private lateinit var resolveInfo : ResolveInfo

                init {
                    nameTextView.setOnClickListener(this)
                }

                fun bindActivity(resolveInfo: ResolveInfo){
                this.resolveInfo= resolveInfo
                    val packageManager = itemView.context.packageManager
                    val appname = resolveInfo.loadLabel(packageManager).toString()
                    val appicon = resolveInfo.loadIcon(packageManager).toBitmap()
                    appIcon.setImageBitmap(appicon)
                    nameTextView.text = appname
                }

        override fun onClick(v: View?) { //page. 454

            val activityInfo = resolveInfo.activityInfo
            val intent = Intent(Intent.ACTION_MAIN).apply {
                setClassName(activityInfo.applicationInfo.packageName, activityInfo.name)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) //page 460
            }
            val context = v?.context
            context?.startActivity(intent)
        }
    }

    private class ActivityAdapter (val activities : List<ResolveInfo>):
        RecyclerView.Adapter<ActivityHolder>(){

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActivityHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val view = layoutInflater.inflate(android.R.layout.activity_list_item, parent, false)
            return ActivityHolder(view)
        }

        override fun onBindViewHolder(holder: ActivityHolder, position: Int) {
            val resolveInfo = activities[position]
            holder.bindActivity(resolveInfo)
        }

        override fun getItemCount(): Int {
            return activities.size
        }

    }

    private fun setupAdapter() { //page: 450
        val startupIntent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }
        val activities = packageManager.queryIntentActivities(startupIntent,0)
        activities.sortWith(Comparator{a,b ->
            String.CASE_INSENSITIVE_ORDER.compare(
                a.loadLabel(packageManager).toString(),
                b.loadLabel(packageManager).toString()
            )
        })

        Log.i(TAG, "Found ${activities.size} activities.")
        recyclerView.adapter = ActivityAdapter(activities)
    }
}