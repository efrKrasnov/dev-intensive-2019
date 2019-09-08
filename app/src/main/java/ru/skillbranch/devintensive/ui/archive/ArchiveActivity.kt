package ru.skillbranch.devintensive.ui.archive

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_archive.*
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
        setSupportActionBar(toolbar_archive)
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
            Snackbar.make(rv_chat_list_archive, "Click on ${it.title}", Snackbar.LENGTH_LONG).show()
        }
        val divider = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)

        val touchCallback = ChatItemTouchHelperCallback(chatAdapter)    {
            viewModel.restoreFromArchive(it.id)
            val idStr = it.id
            val snackbar = Snackbar.make(rv_chat_list_archive, "Восстановить чат с ${it.title} из архива?", Snackbar.LENGTH_LONG)
            snackbar.setAction("ОТМЕНА") { viewModel.addToArchive(idStr) }
            snackbar.show()
        }

        val touchHelper = ItemTouchHelper(touchCallback)

        touchHelper.attachToRecyclerView(rv_chat_list_archive)

        with(rv_chat_list_archive)  {
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
