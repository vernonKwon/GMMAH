package com.GMMAS.gwon_ocheol.schoolapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.zagum.expandicon.ExpandIconView;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;


public class AnonymousBoard extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, GoogleApiClient.OnConnectionFailedListener {
    int A;
    String K;
    private GoogleSignInOptions gso;
    private GoogleApiClient mGoogleApiClient;
    private GoogleSignInClient mGoogleSignInClient;

    final static String TAG = "Log";
    final int REQUEST_INVITE = 10;
    private int RC_SIGN_IN = 11;

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private FirebaseAuth mAuth;

    //private static final int UPLOAD_DATA_REQUESTCODE = 10;
    private Intent upload_intent;

    private TextView headline;
    private ImageButton upload, search;

    private boolean ifsearch = false;
    //private LinearLayout layout_contents;

    private RecyclerView postRecyclerView; // 게시판

    private List<PostDTO> postDTOs = new ArrayList<>();
    private List<String> uniqueKey_list = new ArrayList<>();

    private Date date = new Date();
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy월 M월 d일");

    private SwipeRefreshLayout swipeRefreshLayout;

    private boolean first_loading = true;

    private int the_number_of_post;
    private int the_number_of_post_temp;

    private View parentLayout;
    private EditText search_edittext;

    private AES256Util aes256Util;
    private String temp;

    class BoardRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        View view;
        private List<CommentDTO> commentDTOS = new ArrayList<>();
        private List<String> commentsKeys = new ArrayList<>();
        CommentDTO commentDTO = new CommentDTO();
        Boolean seecomment_boolean = false;

        class CommentRecyclerView extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

            View view;

            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                view = LayoutInflater.from(parent.getContext()).inflate(com.GMMAS.gwon_ocheol.schoolapp.R.layout.comment_design, parent, false);

                return new Comment(view);
                //xml 디자인 한 부분 적용

            } // 완료

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
                //xml 디자인 안의 내용 변경

                ((Comment) holder).comment_content_textView.setText(commentDTOS.get(position).comment);
                ((Comment) holder).comment_date_textView.setText(commentDTOS.get(position).date);
                ((Comment) holder).comment_nickname_textView.setText(commentDTOS.get(position).nickname);

                ((Comment) holder).comment_content_textView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        Toast.makeText(getApplicationContext(), "댓글 수정, 삭제 기능은 개발중입니다.", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                });
            }

            @Override
            public int getItemCount() {

                //아이템을 측정하는 카운터
                return commentDTOS.size();
            }

            class Comment extends RecyclerView.ViewHolder {

                TextView comment_nickname_textView, comment_content_textView, comment_date_textView;

                public Comment(View itemView) {
                    super(itemView);
                    comment_nickname_textView = (TextView) itemView.findViewById(com.GMMAS.gwon_ocheol.schoolapp.R.id.comments_nickname);
                    comment_content_textView = (TextView) itemView.findViewById(com.GMMAS.gwon_ocheol.schoolapp.R.id.comment_content);
                    comment_date_textView = (TextView) itemView.findViewById(com.GMMAS.gwon_ocheol.schoolapp.R.id.comment_date);

                }
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            view = LayoutInflater.from(parent.getContext()).inflate(com.GMMAS.gwon_ocheol.schoolapp.R.layout.postonboard, parent, false);
            return new PostViewer(view);
            //xml 디자인 한 부분 적용
        }

        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
            final int index = position;

            ((PostViewer) holder).seecomment.setState(ExpandIconView.MORE, true);
            seecomment_boolean = false;

            ((PostViewer) holder).subjectView.setText(postDTOs.get(position).subject);
            ((PostViewer) holder).nickname.setText(postDTOs.get(position).nickname);
            ((PostViewer) holder).dateView.setText(postDTOs.get(position).date.substring(0, postDTOs.get(position).date.length() - 7));
            ((PostViewer) holder).comment_linearLayout.setVisibility(View.GONE);
            //((PostViewer) holder).countingOfFeedback_View.setText(String.valueOf(cnt_comment));

            ((PostViewer) holder).upload_comment.setOnClickListener(new View.OnClickListener() { // 댓글 올리기
                @Override
                public void onClick(View view) {
                    if (TextUtils.isEmpty(((PostViewer) holder).comments_password_upload.getText().toString()) |
                            TextUtils.isEmpty(((PostViewer) holder).comments_nickname_upload.getText().toString()) |
                            TextUtils.isEmpty(((PostViewer) holder).upload_comment_editText.getText().toString())) {

                        Toast.makeText(getApplicationContext(), "입력란을 다시 확인해 주세요", Toast.LENGTH_SHORT).show();
                    } else {
                        commentDTO.setPassword(((PostViewer) holder).comments_password_upload.getText().toString());
                        commentDTO.setNickname(((PostViewer) holder).comments_nickname_upload.getText().toString());
                        commentDTO.setComment(((PostViewer) holder).upload_comment_editText.getText().toString());
                        commentDTO.date = simpleDateFormat.format(date);

                        database.getReference().child("post").child(uniqueKey_list.get(index)).child("comments").push().setValue(commentDTO);
                        ((PostViewer) holder).comments_password_upload.setText(null);
                        ((PostViewer) holder).upload_comment_editText.setText(null);
                        ((PostViewer) holder).comments_password_upload.requestFocus();

                    }
                }
            });

            ((PostViewer) holder).seecomment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    //((PostViewer) holder).seecomment.switchState();

                    if (seecomment_boolean) { // 댓글을 펼친 상태에서 댓글을 보지 않으려고 함
                        ((PostViewer) holder).comment_linearLayout.setVisibility(View.GONE);
                        ((PostViewer) holder).seecomment.setState(ExpandIconView.MORE, true);
                        seecomment_boolean = false;
                    } else { // 댓글을 펼치지 않은 상태에서 댓글을 보려고 함
                        ((PostViewer) holder).seecomment.setState(ExpandIconView.LESS, true);
                        database.getReference().child("post").child(uniqueKey_list.get(position)).child("comments").addValueEventListener(new ValueEventListener() { // 댓글 새로고침 리스너, observer pattern
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                commentDTOS.clear();
                                commentsKeys.clear();
                                CommentDTO commentDTO;
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    commentDTO = snapshot.getValue(CommentDTO.class);
                                    commentDTOS.add(commentDTO);
                                    commentsKeys.add(snapshot.getKey());
                                }
                                Log.i(TAG, "snapshot : 완료");
                                ((PostViewer) holder).comment_recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                                final CommentRecyclerView commentRecyclerview = new CommentRecyclerView();
                                ((PostViewer) holder).comment_recyclerView.setAdapter(commentRecyclerview);
                                Log.i(TAG, String.valueOf(position) + " 댓글 로딩 완료");
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Toast.makeText(getApplicationContext(), "WIFI or Cellular data 연결 상태를 확인하세요.", Toast.LENGTH_SHORT).show();
                                Log.e(TAG, "onCancelled: 인터넷 연결 오류");
                            }
                        });
                        ((PostViewer) holder).comment_linearLayout.setVisibility(View.VISIBLE); // 댓글 보기
                        seecomment_boolean = true;
                    }
                }
            });

            if (postDTOs.get(position).contents.getBytes().length <= 50) {
                ((PostViewer) holder).seemore.setVisibility(View.GONE);
                ((PostViewer) holder).seemore.setOnClickListener(null);
                ((PostViewer) holder).contentsView.setText(postDTOs.get(position).contents);
                ((PostViewer) holder).comment_linearLayout.setVisibility(View.GONE);
            } else if (postDTOs.get(position).contents.getBytes().length > 50) {
                ((PostViewer) holder).seemore.setVisibility(View.VISIBLE);
                ((PostViewer) holder).contentsView.setText(getByteString(postDTOs.get(position).contents));

                ((PostViewer) holder).seemore.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Toast.makeText(getApplicationContext(), "더보기 클릭", Toast.LENGTH_SHORT).show();
                        ((PostViewer) holder).contentsView.setText(postDTOs.get(position).contents);
                        ((PostViewer) holder).seemore.setVisibility(View.GONE);
                    }
                });
            }

            ((PostViewer) holder).post_linearLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {

                    ((PostViewer) holder).v_iew = View.inflate(AnonymousBoard.this, com.GMMAS.gwon_ocheol.schoolapp.R.layout.editpost_inflate, null);

                    final AlertDialog.Builder option = new AlertDialog.Builder(AnonymousBoard.this);
                    option.setTitle("메뉴");

                    final EditText password_edittext = (EditText) ((PostViewer) holder).v_iew.findViewById(com.GMMAS.gwon_ocheol.schoolapp.R.id.post_edit_password);
                    final EditText subject_edittext = (EditText) ((PostViewer) holder).v_iew.findViewById(com.GMMAS.gwon_ocheol.schoolapp.R.id.post_subject_password);
                    final EditText contents_edittext = (EditText) ((PostViewer) holder).v_iew.findViewById(com.GMMAS.gwon_ocheol.schoolapp.R.id.Edit_content);

                    option.setView(((PostViewer) holder).v_iew);

                    subject_edittext.setText(postDTOs.get(position).subject);
                    contents_edittext.setText(postDTOs.get(position).contents);

                    try {
                        aes256Util = new AES256Util(mAuth.getCurrentUser().getUid());
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), "알 수 없는 오류가 발생하였습니다", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "AES256 객체 생성 실패");
                    }

                    option.setPositiveButton("글 수정하기", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                if (password_edittext.getText().toString().equals(aes256Util.aesDecode(postDTOs.get(position).password)) |
                                        TextUtils.isEmpty(password_edittext.getText()) | TextUtils.isEmpty(subject_edittext.getText())
                                        | TextUtils.isEmpty(contents_edittext.getText())) {
                                    Log.e(TAG, "복호화 password: " + aes256Util.aesDecode(postDTOs.get(position).password));
                                    postDTOs.get(position).setSubject(subject_edittext.getText().toString());
                                    postDTOs.get(position).setContent(contents_edittext.getText().toString());
                                    database.getReference().child("post").child(uniqueKey_list.get(position)).setValue(postDTOs.get(position));
                                } else {
                                    Toast.makeText(getApplicationContext(), "비밀번호 혹은 빈칸이 있는지 확인하세요.", Toast.LENGTH_SHORT).show();
                                }
                            } catch (Exception e) {
                                Toast.makeText(getApplicationContext(), "글 수정 실패", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    option.setNegativeButton("글 삭제하기", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                if (password_edittext.getText().toString().equals(aes256Util.aesDecode(postDTOs.get(position).password))) {
                                    Log.e(TAG, "복호화 password: " + aes256Util.aesDecode(postDTOs.get(position).password));
                                    database.getReference().child("post").child(uniqueKey_list.get(position)).setValue(null);
                                } else {
                                    Toast.makeText(getApplicationContext(), "비밀번호 혹은 빈칸이 있는지 확인하세요.", Toast.LENGTH_SHORT).show();
                                }
                            } catch (Exception e) {
                                Toast.makeText(getApplicationContext(), "글 삭제 실패", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    option.setNeutralButton("취소", null);
                    option.show();
                    return false;
                }
            });
            //xml 디자인 안의 내용 변경

        }

        @Override
        public int getItemCount() {

            //아이템을 측정하는 카운터
            return postDTOs.size();
        }

        class PostViewer extends RecyclerView.ViewHolder {

            TextView subjectView; // 제목
            TextView dateView; // 날짜
            TextView contentsView; // 게시글
            TextView seemore; // 글 더보기
            TextView nickname; // 닉네임

            Button upload_comment;
            ExpandIconView seecomment;

            LinearLayout post_linearLayout, comment_linearLayout;
            EditText comments_nickname_upload, comments_password_upload, upload_comment_editText;
            RecyclerView comment_recyclerView;

            View v_iew;

            public PostViewer(View view) {
                super(view);

                post_linearLayout = (LinearLayout) view.findViewById(com.GMMAS.gwon_ocheol.schoolapp.R.id.post_linearlayout);

                subjectView = (TextView) view.findViewById(com.GMMAS.gwon_ocheol.schoolapp.R.id.subject);
                dateView = (TextView) view.findViewById(com.GMMAS.gwon_ocheol.schoolapp.R.id.date);
                contentsView = (TextView) view.findViewById(com.GMMAS.gwon_ocheol.schoolapp.R.id.post);
                seemore = (TextView) view.findViewById(com.GMMAS.gwon_ocheol.schoolapp.R.id.moreseepost);
                nickname = (TextView) view.findViewById(com.GMMAS.gwon_ocheol.schoolapp.R.id.shownickname);

                //post_share = (Button) view.findViewById(R.id.share);
                seecomment = (ExpandIconView) view.findViewById(com.GMMAS.gwon_ocheol.schoolapp.R.id.see_comment);

                comment_linearLayout = (LinearLayout) view.findViewById(com.GMMAS.gwon_ocheol.schoolapp.R.id.postComment); //setVisible(); 설정하기 위함

                upload_comment = (Button) view.findViewById(com.GMMAS.gwon_ocheol.schoolapp.R.id.upload_comment);
                comments_nickname_upload = (EditText) view.findViewById(com.GMMAS.gwon_ocheol.schoolapp.R.id.comments_nickname_upload); // edittext
                comments_password_upload = (EditText) view.findViewById(com.GMMAS.gwon_ocheol.schoolapp.R.id.comments_password_upload); // edittext
                upload_comment_editText = (EditText) view.findViewById(com.GMMAS.gwon_ocheol.schoolapp.R.id.upload_comment_edittext); // edittext

                comment_recyclerView = (RecyclerView) view.findViewById(com.GMMAS.gwon_ocheol.schoolapp.R.id.SeeComment);
            }
        }

        public String getByteString(String s) {
            temp = new String(s.getBytes(), 0, 50).trim(); // startIdx, bytes
            return temp;
        }
    }

    final BoardRecyclerViewAdapter boardRecyclerViewAdapter = new BoardRecyclerViewAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.GMMAS.gwon_ocheol.schoolapp.R.layout.activity_ananymous_board);
        //database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        login();

        //layout_contents = (LinearLayout) findViewById(R.id.activity_content_view);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(com.GMMAS.gwon_ocheol.schoolapp.R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this);

        postRecyclerView = (RecyclerView) findViewById(com.GMMAS.gwon_ocheol.schoolapp.R.id.RecyclerView);
        postRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        postRecyclerView.setAdapter(boardRecyclerViewAdapter);

        parentLayout = findViewById(com.GMMAS.gwon_ocheol.schoolapp.R.id.anaymous_board);
        search_edittext = (EditText) findViewById(com.GMMAS.gwon_ocheol.schoolapp.R.id.search_edittext);

        database.getReference().child("post").limitToLast(100).addValueEventListener(new ValueEventListener() { //새로운 소식
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                postDTOs.clear();
                uniqueKey_list.clear();
                PostDTO postDTO;
                try {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        postDTO = snapshot.getValue(PostDTO.class);
                        postDTOs.add(postDTO);
                        uniqueKey_list.add(snapshot.getKey());
                    }
                } catch (Exception e) {
                    //System.out.println(e.toString());
                }
                the_number_of_post = postDTOs.size();
                Collections.reverse(postDTOs); // 게사글 list reverse
                Collections.reverse(uniqueKey_list); // 게시글에 따른 댓글 list reverse

                check();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "WIFI or Cellular data 연결 상태를 확인하세요.", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "onCancelled: " + databaseError.toString());
                Log.e(TAG, "onCancelled: 인터넷 연결 오류");
            }
        });

        upload = (ImageButton) findViewById(com.GMMAS.gwon_ocheol.schoolapp.R.id.upload_post);
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getApplicationContext(), "upload", Toast.LENGTH_SHORT).show();

                upload_intent = new Intent(getApplicationContext(), PostSetData.class);
                startActivity(upload_intent);
            }
        });

        /*search = (ImageButton) findViewById(R.id.search_post);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ifsearch) { // 검색을 하지 않기 위해 실행
                    ifsearch = false;
                    search_edittext.setVisibility(View.GONE);
                } else { // ifsearch == false, 검색을 시작할때 실행
                    ifsearch = true;
                    search_edittext.setVisibility(View.VISIBLE);
                }
                Toast.makeText(getApplicationContext(), "search", Toast.LENGTH_SHORT).show();
            }
        });*/
        setfont();
    }

    @Override
    public void onRefresh() {

        swipeRefreshLayout.setRefreshing(false);

        swipeRefreshLayout.setColorSchemeColors(
                getResources().getColor(android.R.color.holo_blue_bright),
                getResources().getColor(android.R.color.holo_green_light),
                getResources().getColor(android.R.color.holo_orange_light),
                getResources().getColor(android.R.color.holo_red_light)
        );
        boardRecyclerViewAdapter.notifyDataSetChanged();
        //setView(boardRecyclerViewAdapter);
    }

    private void login() {

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(com.GMMAS.gwon_ocheol.schoolapp.R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this, this).
                addApi(Auth.GOOGLE_SIGN_IN_API, gso).build();

        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
       /* mAuth.signInAnonymously().addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {// 로그인 하기
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    System.out.println("Authentication success");
                } else {
                    // If sign in fails, display a message to the user.
                    System.out.println("Authentication failed");
                }
            }

        });
        Log.i(TAG, "login: 완료");*/
    }

    private void setfont() {
        headline = (TextView) findViewById(com.GMMAS.gwon_ocheol.schoolapp.R.id.Headline_Post_Board);
        Typeface typeFace = Typeface.createFromAsset(getApplicationContext().getAssets(), "font/KBIZ한마음고딕.ttf");
        headline.setTypeface(typeFace);//제목 글씨체 바꾸기
        Log.i(TAG, "setfont: 완료");
    }

    public void setFirst_loading(boolean first_loading) {
        this.first_loading = first_loading;
    }

    public boolean isFirst_loading() {
        return first_loading;
    }

    private void check() {
        if (isFirst_loading()) {
            the_number_of_post_temp = the_number_of_post;
            boardRecyclerViewAdapter.notifyDataSetChanged();

            Log.e(TAG, "isFirst_loading " + isFirst_loading());
            Log.e(TAG, "the_number_of_post_temp : " + the_number_of_post_temp);
            Log.e(TAG, "the_number_of_post : " + the_number_of_post);

            setFirst_loading(false);

        } else {
            Log.e(TAG, "isFirst_loading " + isFirst_loading());
            Log.e(TAG, "the_number_of_post_temp : " + the_number_of_post_temp);
            Log.e(TAG, "the_number_of_post : " + the_number_of_post);

            if (the_number_of_post > the_number_of_post_temp) {

                Refresh_Board_snackbar("새로운 글이 올라왔습니다");

                the_number_of_post_temp = the_number_of_post;
            } else if (the_number_of_post == the_number_of_post_temp) {
                Log.i(TAG, "글 수엔 변화 없음");
                Log.e(TAG, "the_number_of_post_temp : " + the_number_of_post_temp);
                Log.e(TAG, "the_number_of_post : " + the_number_of_post);
            } else {

                Refresh_Board_snackbar("게시판을 새로고침 해주세요");
            }
        }
    }

    private void Refresh_Board_snackbar(String a) {
        Snackbar.make(parentLayout, a, Snackbar.LENGTH_LONG).setAction("Action", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boardRecyclerViewAdapter.notifyDataSetChanged();
            }
        }).setActionTextColor(getResources().getColor(android.R.color.holo_red_dark)).show();
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            //FirebaseUser user = mAuth.getCurrentUser();

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(getApplicationContext(), "Authentication failed. 로그인 실패", Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: requestCode=" + requestCode + ", resultCode=" + resultCode);

        /*if (requestCode == REQUEST_INVITE) {
            if (resultCode == RESULT_OK) {

                Toast.makeText(getApplicationContext(),"익명 로그인",Toast.LENGTH_SHORT).show();
                // Get the invitation IDs of all sent messages
                String[] ids = AppInviteInvitation.getInvitationIds(resultCode, data);
                for (String id : ids) {
                    Log.d(TAG, "onActivityResult: sent invitation " + id);
                }
            } else {
            }
        }*/
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {

                // Google Sign In failed, update UI appropriately
                // ...
            }
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(getApplicationContext(), "연결 실패", Toast.LENGTH_SHORT).show();
    }

    /*private void delete_comment(int a, String k*//*final DatabaseReference ref, final String p, final String e*//*) {
        A = a;
        K= k;
        View view = View.inflate(AnonymousBoard.this, R.layout.editcomment_inflate, null);
        edit_comment = (EditText) view.findViewById(R.id.edit_comment_content);
        edit_comment_password = (EditText) view.findViewById(R.id.edit_comment_password);
        AlertDialog.Builder option = new AlertDialog.Builder(AnonymousBoard.this);
        option.setTitle("메뉴");
        option.setView(view);

        edit_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AnonymousBoard.this, "댓글 수정", Toast.LENGTH_SHORT).show();
            }
        });

        edit_comment_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AnonymousBoard.this, "댓글 패스워드", Toast.LENGTH_SHORT).show();
            }
        });
        option.setPositiveButton("수정하기", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                database.getReference().child("post").child(uniqueKey_list.get(A)).child("comments").child(K).setValue(null);
                Toast.makeText(getApplicationContext(), "댓글 커밋", Toast.LENGTH_SHORT).show();
*//*try{
                    if(e.equals(aes256Util.aesDecode(p))){
                        ref.setValue(null);
                    }
                } catch (Exception e){
                    Toast.makeText(AnonymousBoard.this, "댓글 삭제 실패하였습니다", Toast.LENGTH_SHORT).show();
                }*//*
            }
        });


        option.show();
    }*/


}