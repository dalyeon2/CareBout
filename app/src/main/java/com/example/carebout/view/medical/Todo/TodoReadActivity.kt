package com.example.carebout.view.medical.Todo

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.carebout.R
import com.example.carebout.databinding.ActivityTodoReadBinding
import com.example.carebout.view.medical.db.AppDatabase
import com.example.carebout.view.medical.db.DailyTodo
import com.example.carebout.view.medical.db.TodoDao
import com.google.android.material.floatingactionbutton.FloatingActionButton

class TodoReadActivity : AppCompatActivity() {
    //, OnItemLongClickListener
    private lateinit var binding: ActivityTodoReadBinding

    private lateinit var db: AppDatabase
    private lateinit var todoDao: TodoDao
    private var todoList: ArrayList<DailyTodo> = ArrayList<DailyTodo>()
    private lateinit var adapter: TodoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTodoReadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //db 인스턴스를 가져오고 db작업을 할 수 있는 dao를 가져옵니다.
        db = AppDatabase.getInstance(this)!!
        todoDao = db.getTodoDao()

        val insertBtn: FloatingActionButton = findViewById(R.id.insert_btn)
        insertBtn.setOnClickListener {
            val intent: Intent = Intent(this, TodoWriteActivity::class.java)

            activityResult.launch(intent)
        }

        //getAllTodoList() // 할 일 ㅅ리스트 가져오기

        //RecyclerView 설정
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        //UserAdapter 초기화
        adapter = TodoAdapter(this)

        //Adapter 적용
        recyclerView.adapter = adapter

        // todoList 초기화
        //todoList = ArrayList()

        //조회
        getAllTodoList()

    }

    //액티비티가 백그라운드에 있는데 호출되면 실행 /수정화면에서 호출시 실행
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        //사용자 조회
        getAllTodoList()
    }

    private val activityResult: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if(it.resultCode == RESULT_OK){
            //들어온 값이 OK라면
            //리스트 조회
            getAllTodoList()
        }
    }
    //리스트 조회
    private fun getAllTodoList() {

        val todoList: ArrayList<DailyTodo> = db?.getTodoDao()!!.getTodoAll() as ArrayList<DailyTodo>

        if(todoList.isNotEmpty()){
            //데이터 적용
            adapter.setTodoList(todoList)

        } else {
            adapter.setTodoList(ArrayList())
        }
    }

}