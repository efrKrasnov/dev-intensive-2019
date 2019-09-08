package ru.skillbranch.devintensive.ui.archive

import android.content.Intent
import android.os.Bundle
import android.util.TypedValue
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_archive.*
import kotlinx.android.synthetic.main.activity_archive.toolbar
import ru.skillbranch.devintensive.R
import ru.skillbranch.devintensive.ui.adapters.ChatAdapter
import ru.skillbranch.devintensive.ui.adapters.ChatItemTouchHelperCallback
import ru.skillbranch.devintensive.ui.group.GroupActivity
import ru.skillbranch.devintensive.viewmodels.ArchiveViewModel


class ArchiveActivity : AppCompatActivity() {

    private lateinit var viewModel: ArchiveViewModel
    private lateinit var chatAdapter: ChatAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_archive)
        initToolbar()
        initViews()
        initViewModel()
        supportActionBar!!.title="Архив чатов"
    }

    override fun onCreateOptionsMenu(menu: Menu?):Boolean   {
        menuInflater.inflate(R.menu.menu_search, menu)
        val searchItem = menu?.findItem(R.id.action_search)
        val searchView = searchItem?.actionView as SearchView
        searchView.queryHint = "Введите имя пользователя"
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener   {
            override fun onQueryTextSubmit(query: String?): Boolean {
                viewModel.handleSearchQuery(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.handleSearchQuery(newText)
                return true
            }
        })
        return super.onCreateOptionsMenu(menu)
    }
    private fun initToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return if(item?.itemId == android.R.id.home)   {
            finish()
            overridePendingTransition(R.anim.idle, R.anim.bottom_down)
            true
        } else    {
            super.onOptionsItemSelected(item)
        }
    }

    private fun initViews() {

        chatAdapter = ChatAdapter {
            val snackbar = Snackbar.make(rv_archive_list, "Click on ${it.title}", Snackbar.LENGTH_LONG)
            snackbar.view.setBackgroundResource(R.drawable.bg_snackbar)

            val typedValue = TypedValue()
            val view = snackbar.view
            val textView = view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
            theme.resolveAttribute(R.attr.colorSnackbarText, typedValue, true)
            textView.setTextColor(typedValue.data)

            snackbar.show()
        }
        val divider = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)

        val touchCallback = ChatItemTouchHelperCallback(chatAdapter)    {
            viewModel.restoreFromArchive(it.id)
            val idStr = it.id
            val snackbar = Snackbar.make(rv_archive_list, "Восстановить чат с ${it.title} из архива?", Snackbar.LENGTH_LONG)
            snackbar.setAction("ОТМЕНА") { viewModel.addToArchive(idStr) }
            snackbar.view.setBackgroundResource(R.drawable.bg_snackbar)

            val typedValue = TypedValue()
            val view = snackbar.view
            val textView = view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
            theme.resolveAttribute(R.attr.colorSnackbarText, typedValue, true)
            textView.setTextColor(typedValue.data)

            snackbar.show()
        }

        val touchHelper = ItemTouchHelper(touchCallback)

        touchHelper.attachToRecyclerView(rv_archive_list)

        with(rv_archive_list)  {
            adapter = chatAdapter
            layoutManager = LinearLayoutManager(this@ArchiveActivity)
            addItemDecoration(divider)
        }

        fab_archive.setOnClickListener{
            val intent = Intent(this, GroupActivity::class.java)
            startActivity(intent)
        }
    }

    private fun initViewModel() {
        viewModel = ViewModelProviders.of(this).get(ArchiveViewModel::class.java)
        viewModel.getChatData().observe(this, Observer { chatAdapter.updateData(it) })
    }



}
