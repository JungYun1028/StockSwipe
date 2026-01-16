import { useState, useRef, useEffect } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { MessageCircle, X, Send, Sparkles, Loader2 } from 'lucide-react';
import { useApp } from '../context/AppContext';
import styles from './FloatingChatbot.module.css';

// Mock API response for MVP (실제 프로덕션에서는 OpenAI API 연동)
const getMockResponse = async (message, context) => {
  await new Promise(resolve => setTimeout(resolve, 1000 + Math.random() * 1000));
  
  const lowerMessage = message.toLowerCase();
  
  if (context?.type === 'stock' && context.data) {
    const stockName = context.data.name;
    const ticker = context.data.ticker;
    const currentPrice = context.data.currentPrice;
    const rsi = context.data.rsi ?? 50;
    
    if (lowerMessage.includes('rsi')) {
      return `RSI(상대강도지수)는 주가의 상승/하락 압력을 측정하는 지표예요.\n\n${stockName}의 현재 RSI는 ${rsi}인데요,\n• 70 이상: 과매수 구간 (조정 가능성)\n• 30 이하: 과매도 구간 (반등 가능성)\n\n현재는 ${rsi >= 70 ? '과매수 구간에 진입해 단기 조정에 유의하세요.' : rsi <= 30 ? '과매도 구간으로 반등 가능성을 기대해볼 수 있어요.' : '중립적인 구간이에요.'}`;
    }
    
    if (lowerMessage.includes('단기') || lowerMessage.includes('장기')) {
      return `${stockName}(${ticker}) 투자 기간에 대해 알려드릴게요.\n\n📊 단기 관점:\n• 현재가 ${currentPrice?.toLocaleString()}원 기준으로 변동성을 고려한 매매가 필요해요\n• RSI ${rsi} 수준에서 ${rsi >= 60 ? '단기 과열 신호가 있어요' : '아직 여유가 있어요'}\n\n📈 장기 관점:\n• 섹터 성장성과 기업 펀더멘털을 고려해주세요\n• 이동평균선 추세를 확인하면 좋아요`;
    }
    
    if (lowerMessage.includes('매수') || lowerMessage.includes('매도') || lowerMessage.includes('살까')) {
      return `투자 결정은 개인의 투자 성향과 리스크 허용 범위에 따라 다르지만,\n\n${stockName}에 대한 현재 분석을 알려드릴게요:\n\n• 현재가: ${currentPrice?.toLocaleString()}원\n• 기술적 지표: RSI ${rsi}\n• 시장 상황에 따른 변동성 고려 필요\n\n⚠️ 이 정보는 참고용이며, 실제 투자 결정은 본인의 판단과 책임 하에 이루어져야 해요.`;
    }
    
    return `${stockName}(${ticker})에 대해 궁금한 점을 더 구체적으로 물어봐 주세요!\n\n예시 질문:\n• "RSI가 높다는 건 무슨 뜻이야?"\n• "이 종목은 단기 투자야, 장기야?"\n• "현재 시장 상황은 어때?"`;
  }
  
  // General responses
  if (lowerMessage.includes('안녕') || lowerMessage.includes('hello')) {
    return '안녕하세요! 주식 투자에 대해 궁금한 점을 물어봐 주세요. 🙌\n\n종목 분석, 뉴스 해석, 지표 설명 등 다양한 질문에 답변해드릴게요.';
  }
  
  if (lowerMessage.includes('도움') || lowerMessage.includes('help')) {
    return '저는 다음과 같은 질문에 답변할 수 있어요:\n\n📊 종목 분석\n• RSI, 이동평균 등 기술적 지표 설명\n• 종목별 투자 의견\n\n📰 뉴스 해석\n• 뉴스 키워드 설명\n• 시장 영향 분석\n\n💡 투자 정보\n• 용어 설명\n• 섹터별 트렌드';
  }
  
  return '죄송해요, 질문을 정확히 이해하지 못했어요.\n\n현재 보고 계신 종목이나 뉴스에 대해 구체적으로 질문해 주시면 더 정확한 답변을 드릴 수 있어요!\n\n예: "RSI가 뭐야?", "이 종목 전망은 어때?"';
};

const FloatingChatbot = () => {
  const { isChatOpen, setIsChatOpen, chatMessages, addChatMessage, chatContext, clearChat } = useApp();
  const [input, setInput] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const messagesEndRef = useRef(null);
  const inputRef = useRef(null);
  
  const suggestedQuestions = chatContext?.type === 'stock' && chatContext.data
    ? [
        'RSI가 높다는 건 무슨 뜻이야?',
        '이 종목은 단기 투자야, 장기야?',
        '현재 시장 상황은 어때?',
      ]
    : [
        '어떤 도움을 받을 수 있어?',
        '투자 전략 추천해줘',
        '오늘 시장 이슈가 뭐야?',
      ];
  
  useEffect(() => {
    if (messagesEndRef.current) {
      messagesEndRef.current.scrollIntoView({ behavior: 'smooth' });
    }
  }, [chatMessages]);
  
  useEffect(() => {
    if (isChatOpen && inputRef.current) {
      inputRef.current.focus();
    }
  }, [isChatOpen]);
  
  const handleSend = async () => {
    if (!input.trim() || isLoading) return;
    
    const userMessage = input.trim();
    setInput('');
    addChatMessage('user', userMessage);
    setIsLoading(true);
    
    try {
      // MVP: Mock API response
      // 프로덕션에서는 여기서 OpenAI API를 호출
      const response = await getMockResponse(userMessage, chatContext);
      addChatMessage('assistant', response);
    } catch {
      addChatMessage('assistant', '죄송해요, 잠시 오류가 발생했어요. 다시 시도해주세요.');
    } finally {
      setIsLoading(false);
    }
  };
  
  const handleSuggestedQuestion = (question) => {
    setInput(question);
    setTimeout(() => handleSend(), 100);
  };
  
  return (
    <>
      {/* Floating Button */}
      <motion.button
        className={styles.floatingButton}
        onClick={() => setIsChatOpen(!isChatOpen)}
        whileHover={{ scale: 1.05 }}
        whileTap={{ scale: 0.95 }}
        animate={{ rotate: isChatOpen ? 180 : 0 }}
      >
        {isChatOpen ? <X size={24} /> : <MessageCircle size={24} />}
      </motion.button>
      
      {/* Chat Panel */}
      <AnimatePresence>
        {isChatOpen && (
          <motion.div
            className={styles.chatPanel}
            initial={{ opacity: 0, y: 20, scale: 0.95 }}
            animate={{ opacity: 1, y: 0, scale: 1 }}
            exit={{ opacity: 0, y: 20, scale: 0.95 }}
            transition={{ duration: 0.25 }}
          >
            {/* Header */}
            <div className={styles.chatHeader}>
              <div className={styles.headerInfo}>
                <Sparkles size={18} className={styles.sparkle} />
                <span>AI 투자 어시스턴트</span>
              </div>
              <button className={styles.clearButton} onClick={clearChat}>
                대화 초기화
              </button>
            </div>
            
            {/* Context indicator */}
            {chatContext?.type === 'stock' && chatContext.data && (
              <div className={styles.contextBar}>
                📊 {chatContext.data.name} ({chatContext.data.ticker}) 분석 중
              </div>
            )}
            
            {/* Messages */}
            <div className={styles.messagesContainer}>
              {chatMessages.length === 0 ? (
                <div className={styles.welcomeMessage}>
                  <span className={styles.welcomeIcon}>👋</span>
                  <h3>안녕하세요!</h3>
                  <p>주식에 대해 궁금한 점을 물어보세요</p>
                  
                  <div className={styles.suggestedQuestions}>
                    {suggestedQuestions.map((q, i) => (
                      <button
                        key={i}
                        className={styles.suggestedButton}
                        onClick={() => handleSuggestedQuestion(q)}
                      >
                        {q}
                      </button>
                    ))}
                  </div>
                </div>
              ) : (
                <>
                  {chatMessages.map((msg, index) => (
                    <motion.div
                      key={index}
                      className={`${styles.message} ${msg.role === 'user' ? styles.userMessage : styles.assistantMessage}`}
                      initial={{ opacity: 0, y: 10 }}
                      animate={{ opacity: 1, y: 0 }}
                      transition={{ delay: 0.1 }}
                    >
                      <div className={styles.messageContent}>
                        {msg.content.split('\n').map((line, i) => (
                          <span key={i}>
                            {line}
                            {i < msg.content.split('\n').length - 1 && <br />}
                          </span>
                        ))}
                      </div>
                    </motion.div>
                  ))}
                  {isLoading && (
                    <div className={`${styles.message} ${styles.assistantMessage}`}>
                      <div className={styles.loadingIndicator}>
                        <Loader2 size={16} className={styles.spinner} />
                        <span>분석 중...</span>
                      </div>
                    </div>
                  )}
                </>
              )}
              <div ref={messagesEndRef} />
            </div>
            
            {/* Input */}
            <div className={styles.inputContainer}>
              <input
                ref={inputRef}
                type="text"
                value={input}
                onChange={(e) => setInput(e.target.value)}
                onKeyDown={(e) => e.key === 'Enter' && handleSend()}
                placeholder="질문을 입력하세요..."
                className={styles.input}
                disabled={isLoading}
              />
              <button
                className={styles.sendButton}
                onClick={handleSend}
                disabled={!input.trim() || isLoading}
              >
                <Send size={18} />
              </button>
            </div>
          </motion.div>
        )}
      </AnimatePresence>
    </>
  );
};

export default FloatingChatbot;

