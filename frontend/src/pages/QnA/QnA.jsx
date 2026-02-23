import React, { useState, useEffect, useCallback } from 'react';
import axios from 'axios';
import * as S from './QnA.styled';
import { useTheme } from 'styled-components'; // Import useTheme
import {
  Search, MessageSquare, Eye, MessageCircle, Heart, CheckCircle2, // 이거 추가
  Clock
} from 'lucide-react';
import { useNavigate } from 'react-router-dom';
const QnA = () => {
  const theme = useTheme(); // Get theme context
  const [searchQuery, setSearchQuery] = useState('');
  const [selectedFilter, setSelectedFilter] = useState('최신순');
  const [qnaList, setQnaList] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [newPost, setNewPost] = useState({ title: '', content: '' });
  const [currentPage, setCurrentPage] = useState(0); // 백엔드는 0번부터 시작
  const [totalPages, setTotalPages] = useState(1);
  const navigate = useNavigate();

  // 2. API 호출 로직

  const fetchQuestions = useCallback(async () => {
    try {
      setIsLoading(true);
      // 백엔드 엔드포인트에 page와 size 전달
      const response = await axios.get(`/api/questions?page=${currentPage}&size=10`);

      // Page 객체에서 content(목록)와 totalPages(전체 페이지) 추출
      const { content, totalPages } = response.data;

      setQnaList(content || []);
      setTotalPages(totalPages || 1);

      console.log("📦 받은 데이터:", response.data);
    } catch (error) {
      console.error("데이터를 불러오는데 실패했습니다:", error);
    } finally {
      setIsLoading(false);
    }
  }, [currentPage]); // 페이지 바뀔 때마다 실행

  useEffect(() => {
    fetchQuestions();
  }, [fetchQuestions]);



  const filteredList = qnaList.filter((item) => {
    const query = searchQuery.toLowerCase();

    // 제목, 내용, 작성자 이름 중 검색어가 포함된 것이 있는지 확인
    return (
      item.questionTitle?.toLowerCase().includes(query) ||
      item.questionContent?.toLowerCase().includes(query) ||
      item.questionerName?.toLowerCase().includes(query)
    );
  });

  // 2. handleSearch 함수는 이제 엔터를 쳤을 때 새로고침 방지만 하면 됩니다.
  const handleSearch = (e) => {
    e.preventDefault(); // 페이지 새로고침 방지
    // 실제 필터링은 위에서 실시간(filteredList)으로 일어납니다.
  };

  const handleCreatePost = async (e) => {
    e.preventDefault();

    try {
      // 1. 변수명을 storageData로 통일
      const storageData = localStorage.getItem('auth-storage');

      // 콘솔에 찍어서 데이터가 잘 나오는지 확인 (개발용)
      console.log("로컬스토리지 데이터:", storageData);

      if (!storageData) {
        alert("로그인 정보가 없습니다. 다시 로그인 해주세요.");
        return;
      }

      // 2. 파싱 로직
      const parsedData = JSON.parse(storageData);
      const token = parsedData.state?.token;

      if (!token) {
        alert("유효한 토큰이 없습니다. 다시 로그인해주세요.");
        return;
      }

      // 3. API 요청
      const response = await axios.post('/api/questions', {
        title: newPost.title,
        content: newPost.content
      }, {
        headers: {
          Authorization: `Bearer ${token}`,
          "Content-Type": "application/json"
        }
      });

      alert("글이 성공적으로 등록되었습니다.");
      setIsModalOpen(false);
      setNewPost({ title: '', content: '' });
      fetchQuestions(); // 리스트 새로고침
      console.log("6. ✅ 서버 응답 결과:", response.data);
    } catch (error) {
      console.error("등록 실패 상세:", error);
      alert("등록 중 오류가 발생했습니다.");
    }
  };

  return (
    <S.PageContainer>
      <S.MainContent>
        <S.ContentWrapper>
          <S.PageHeader>
            <S.PageTitle>
              <MessageSquare size={28} />
              Q&A
            </S.PageTitle>
            <S.CreateButton onClick={() => setIsModalOpen(true)}>+ 글쓰기</S.CreateButton>
          </S.PageHeader>

          {/* 2. 글쓰기 모달 영역 */}
          {isModalOpen && (
            <S.ModalOverlay onClick={() => setIsModalOpen(false)}>
              <S.ModalContainer onClick={(e) => e.stopPropagation()}>
                <S.ModalHeader>
                  <h3>질문하기</h3>
                  <button onClick={() => setIsModalOpen(false)}>×</button>
                </S.ModalHeader>
                <S.ModalBody onSubmit={handleCreatePost}>
                  <S.FormGroup>
                    <label>제목</label>
                    <input
                      type="text"
                      placeholder="제목을 입력하세요"
                      value={newPost.title}
                      onChange={(e) => setNewPost({ ...newPost, title: e.target.value })}
                    />
                  </S.FormGroup>
                  <S.FormGroup>
                    <label>내용</label>
                    <textarea
                      placeholder="내용을 상세히 입력해주세요"
                      rows="10"
                      value={newPost.content}
                      onChange={(e) => setNewPost({ ...newPost, content: e.target.value })}
                    />
                  </S.FormGroup>
                  <S.ModalFooter>
                    <S.CancelButton type="button" onClick={() => setIsModalOpen(false)}>취소</S.CancelButton>
                    <S.SubmitButton type="submit">등록하기</S.SubmitButton>
                  </S.ModalFooter>
                </S.ModalBody>
              </S.ModalContainer>
            </S.ModalOverlay>
          )}


          <S.SearchSection>
            <S.SearchForm onSubmit={handleSearch}>
              <S.SearchInput
                type="text"
                placeholder="제목, 내용, 작성자를 검색..."
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
              />
              <S.SearchButton type="submit">
                <Search size={20} />
              </S.SearchButton>
            </S.SearchForm>
            <S.FilterButton
              $active={selectedFilter === '최신순'}
              onClick={() => setSelectedFilter('최신순')}
            >
              최신순
            </S.FilterButton>
          </S.SearchSection>
          <S.QnaList>
            {filteredList.map((item) => (
              <S.QnaItem key={item.questionId}
                onClick={() => navigate(`/qna/${item.questionId}`)}>
                <S.CategoryBadge
                  $bgColor={item.answered ? theme.status.success : theme.status.warning}
                  $textColor={item.answered ? theme.text.inverse : theme.text.primary}
                >
                  {/* 상태에 따른 아이콘 렌더링 */}
                  {item.answered ? (
                    <CheckCircle2 size={14} style={{ marginRight: '4px' }} />
                  ) : (
                    <Clock size={14} style={{ marginRight: '4px' }} />
                  )}
                  {item.answered ? '답변완료' : '답변대기'}
                </S.CategoryBadge>

                <S.QnaContent>
                  <S.QnaTitle>{item.questionTitle}</S.QnaTitle>
                  <S.QnaMetaRow>
                    <S.QnaMeta>
                      <S.MetaItem>
                        <S.MetaIcon>👤</S.MetaIcon>
                        {item.questionerName}
                      </S.MetaItem>
                      <S.MetaItem>
                        <S.MetaIcon>📅</S.MetaIcon>
                        {item.createDate ? item.createDate.split('T')[0] : '-'}
                      </S.MetaItem>
                    </S.QnaMeta>
                  </S.QnaMetaRow>
                </S.QnaContent>
              </S.QnaItem>
            ))}
          </S.QnaList>
          {/* 페이지네이션 버튼 동적 생성 */}
          <S.Pagination>
            <S.PaginationButton
              onClick={() => setCurrentPage(prev => Math.max(prev - 1, 0))}
              disabled={currentPage === 0}
            >‹</S.PaginationButton>

            {[...Array(totalPages)].map((_, i) => (
              <S.PageNumber
                key={i}
                $active={currentPage === i}
                onClick={() => setCurrentPage(i)}
              >
                {i + 1}
              </S.PageNumber>
            ))}

            <S.PaginationButton
              onClick={() => setCurrentPage(prev => Math.min(prev + 1, totalPages - 1))}
              disabled={currentPage >= totalPages - 1}
            >›</S.PaginationButton>
          </S.Pagination>
        </S.ContentWrapper>
      </S.MainContent>
    </S.PageContainer>
  );
};

export default QnA;