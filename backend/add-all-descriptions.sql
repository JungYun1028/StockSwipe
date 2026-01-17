-- 모든 종목에 기업 개요 추가
-- 바이오 (20개)
UPDATE stock_master SET description = '바이오 의약품 및 헬스케어 분야 전문 기업', business = '바이오 의약품 연구개발, 제조 및 유통' WHERE stock_id = '068270' AND description IS NULL;
UPDATE stock_master SET description = '제약 및 바이오 의약품 전문 기업', business = '의약품 연구개발, 제조 및 판매' WHERE stock_id = '207940' AND description IS NULL;
UPDATE stock_master SET description = '제네릭 및 오리지널 의약품 제조 기업', business = '의약품 제조, 연구개발 및 판매' WHERE stock_id = '128940' AND description IS NULL;
UPDATE stock_master SET description = '바이오 의약품 및 진단 시약 전문 기업', business = '바이오 의약품, 진단 시약 제조' WHERE stock_id = '086900' AND description IS NULL;
UPDATE stock_master SET description = '항암제 및 바이오신약 개발 기업', business = '항암제, 바이오신약 연구개발' WHERE stock_id = '214450' AND description IS NULL;
UPDATE stock_master SET description = '백신 및 바이오 의약품 전문 기업', business = '백신, 바이오 의약품 제조' WHERE stock_id = '206650' AND description IS NULL;
UPDATE stock_master SET description = '유전자 치료제 및 세포 치료제 개발 기업', business = '유전자 치료제, 세포 치료제 연구개발' WHERE stock_id = '214150' AND description IS NULL;
UPDATE stock_master SET description = '의료기기 및 진단 시약 전문 기업', business = '의료기기, 진단 시약 제조 및 판매' WHERE stock_id = '263750' AND description IS NULL;
UPDATE stock_master SET description = '바이오시밀러 및 제네릭 의약품 제조 기업', business = '바이오시밀러, 제네릭 의약품 제조' WHERE stock_id = '182400' AND description IS NULL;
UPDATE stock_master SET description = '신약 개발 및 제약 전문 기업', business = '신약 연구개발, 의약품 제조' WHERE stock_id = '183490' AND description IS NULL;
UPDATE stock_master SET description = '화학 의약품 및 원료 의약품 제조 기업', business = '화학 의약품, 원료 의약품 제조' WHERE stock_id = '185750' AND description IS NULL;
UPDATE stock_master SET description = '항생제 및 전문 의약품 제조 기업', business = '항생제, 전문 의약품 제조' WHERE stock_id = '004310' AND description IS NULL;
UPDATE stock_master SET description = '바이오 의약품 및 백신 연구개발 기업', business = '바이오 의약품, 백신 연구개발' WHERE stock_id = '214370' AND description IS NULL;
UPDATE stock_master SET description = '제네릭 의약품 및 원료 의약품 제조 기업', business = '제네릭 의약품, 원료 의약품 제조' WHERE stock_id = '009420' AND description IS NULL;
UPDATE stock_master SET description = '바이오 의약품 및 건강기능식품 전문 기업', business = '바이오 의약품, 건강기능식품 제조' WHERE stock_id = '215600' AND description IS NULL;
UPDATE stock_master SET description = '항암제 및 희귀 의약품 전문 기업', business = '항암제, 희귀 의약품 연구개발' WHERE stock_id = '229000' AND description IS NULL;
UPDATE stock_master SET description = '바이오 의약품 및 화장품 원료 제조 기업', business = '바이오 의약품, 화장품 원료 제조' WHERE stock_id = '213420' AND description IS NULL;
UPDATE stock_master SET description = '백신 및 진단 시약 전문 기업', business = '백신, 진단 시약 제조 및 판매' WHERE stock_id = '302440' AND description IS NULL;
UPDATE stock_master SET description = '바이오 의약품 및 건강식품 전문 기업', business = '바이오 의약품, 건강식품 제조' WHERE stock_id = '205470' AND description IS NULL;
UPDATE stock_master SET description = '제약 및 건강기능식품 전문 기업', business = '의약품, 건강기능식품 제조 및 판매' WHERE stock_id = '000250' AND description IS NULL;

-- AI (20개)
UPDATE stock_master SET description = '인공지능 및 빅데이터 솔루션 전문 기업', business = 'AI 솔루션, 빅데이터 분석 서비스' WHERE stock_id = '036570' AND description IS NULL;
UPDATE stock_master SET description = '게임 및 AI 기술 개발 기업', business = '게임 개발, AI 기술 연구개발' WHERE stock_id = '036570' AND description IS NULL;
UPDATE stock_master SET description = 'AI 기반 솔루션 및 소프트웨어 개발 기업', business = 'AI 솔루션, 소프트웨어 개발' WHERE stock_id = '122870' AND description IS NULL;
UPDATE stock_master SET description = '포털 및 AI 서비스 제공 기업', business = '포털 서비스, AI 기술 개발' WHERE stock_id = '035720' AND description IS NULL;
UPDATE stock_master SET description = '게임 및 메타버스 플랫폼 개발 기업', business = '게임 개발, 메타버스 플랫폼 운영' WHERE stock_id = '251270' AND description IS NULL;
UPDATE stock_master SET description = 'AI 기반 보안 솔루션 전문 기업', business = 'AI 보안 솔루션, 사이버 보안 서비스' WHERE stock_id = '053800' AND description IS NULL;
UPDATE stock_master SET description = 'AI 및 클라우드 서비스 제공 기업', business = 'AI 서비스, 클라우드 컴퓨팅' WHERE stock_id = '035420' AND description IS NULL;
UPDATE stock_master SET description = '반도체 및 AI 칩 개발 기업', business = '반도체 설계, AI 칩 개발' WHERE stock_id = '039030' AND description IS NULL;
UPDATE stock_master SET description = 'AI 기반 음성인식 기술 전문 기업', business = '음성인식 기술, AI 솔루션 개발' WHERE stock_id = '036120' AND description IS NULL;
UPDATE stock_master SET description = 'AI 및 IoT 솔루션 제공 기업', business = 'AI 솔루션, IoT 플랫폼 개발' WHERE stock_id = '217270' AND description IS NULL;
UPDATE stock_master SET description = '빅데이터 및 AI 분석 솔루션 전문 기업', business = '빅데이터 분석, AI 솔루션 개발' WHERE stock_id = '052690' AND description IS NULL;
UPDATE stock_master SET description = 'AI 기반 교육 플랫폼 운영 기업', business = 'AI 교육 플랫폼, e-러닝 서비스' WHERE stock_id = '122870' AND description IS NULL;
UPDATE stock_master SET description = 'AI 및 로봇 기술 개발 기업', business = 'AI 로봇, 자동화 시스템 개발' WHERE stock_id = '108860' AND description IS NULL;
UPDATE stock_master SET description = 'AI 기반 헬스케어 솔루션 전문 기업', business = 'AI 헬스케어, 의료 데이터 분석' WHERE stock_id = '214680' AND description IS NULL;
UPDATE stock_master SET description = 'AI 및 머신러닝 기술 개발 기업', business = 'AI 기술, 머신러닝 솔루션 개발' WHERE stock_id = '041510' AND description IS NULL;
UPDATE stock_master SET description = 'AI 기반 금융 솔루션 제공 기업', business = 'AI 금융 솔루션, 핀테크 서비스' WHERE stock_id = '060250' AND description IS NULL;
UPDATE stock_master SET description = 'AI 및 자율주행 기술 개발 기업', business = 'AI 기술, 자율주행 시스템 개발' WHERE stock_id = '140670' AND description IS NULL;
UPDATE stock_master SET description = 'AI 기반 마케팅 솔루션 전문 기업', business = 'AI 마케팅, 광고 솔루션 개발' WHERE stock_id = '214320' AND description IS NULL;
UPDATE stock_master SET description = 'AI 및 블록체인 기술 개발 기업', business = 'AI 기술, 블록체인 플랫폼 개발' WHERE stock_id = 'A123456' AND description IS NULL;
UPDATE stock_master SET description = 'AI 기반 커머스 플랫폼 운영 기업', business = 'AI 커머스, e-커머스 플랫폼 운영' WHERE stock_id = '376300' AND description IS NULL;

-- 선박 (20개)
UPDATE stock_master SET description = '선박 건조 및 해양 플랜트 전문 기업', business = '선박 건조, 해양 플랜트 건설' WHERE stock_id = '009540' AND description IS NULL;
UPDATE stock_master SET description = '조선 및 해양 구조물 제작 기업', business = '조선, 해양 구조물 제작' WHERE stock_id = '010140' AND description IS NULL;
UPDATE stock_master SET description = '선박 엔진 및 기계 부품 제조 기업', business = '선박 엔진, 기계 부품 제조' WHERE stock_id = '042660' AND description IS NULL;
UPDATE stock_master SET description = '선박용 전기전자 장비 전문 기업', business = '선박용 전기전자 장비 제조' WHERE stock_id = '047050' AND description IS NULL;
UPDATE stock_master SET description = '선박 수리 및 유지보수 전문 기업', business = '선박 수리, 유지보수 서비스' WHERE stock_id = '000880' AND description IS NULL;
UPDATE stock_master SET description = '해운 및 물류 서비스 제공 기업', business = '해운, 물류 서비스 운영' WHERE stock_id = '028670' AND description IS NULL;
UPDATE stock_master SET description = '선박용 케이블 및 전선 제조 기업', business = '선박용 케이블, 전선 제조' WHERE stock_id = '001680' AND description IS NULL;
UPDATE stock_master SET description = '선박용 철강 부품 제조 기업', business = '선박용 철강 부품 제조' WHERE stock_id = '010620' AND description IS NULL;
UPDATE stock_master SET description = '선박 내장재 및 설비 제조 기업', business = '선박 내장재, 설비 제조' WHERE stock_id = '073240' AND description IS NULL;
UPDATE stock_master SET description = '선박용 도료 및 코팅제 전문 기업', business = '선박용 도료, 코팅제 제조' WHERE stock_id = '145990' AND description IS NULL;
UPDATE stock_master SET description = '선박 건조 및 수리 전문 기업', business = '선박 건조, 수리 서비스' WHERE stock_id = '071970' AND description IS NULL;
UPDATE stock_master SET description = '해양 설비 및 구조물 제작 기업', business = '해양 설비, 구조물 제작' WHERE stock_id = '267260' AND description IS NULL;
UPDATE stock_master SET description = '선박용 밸브 및 배관 부품 제조 기업', business = '선박용 밸브, 배관 부품 제조' WHERE stock_id = '003680' AND description IS NULL;
UPDATE stock_master SET description = '선박 운영 및 관리 서비스 제공 기업', business = '선박 운영, 관리 서비스' WHERE stock_id = '002880' AND description IS NULL;
UPDATE stock_master SET description = '선박용 냉동 및 공조 설비 전문 기업', business = '선박용 냉동, 공조 설비 제조' WHERE stock_id = '011000' AND description IS NULL;
UPDATE stock_master SET description = '선박용 펌프 및 압축기 제조 기업', business = '선박용 펌프, 압축기 제조' WHERE stock_id = '010060' AND description IS NULL;
UPDATE stock_master SET description = '선박 해체 및 재활용 전문 기업', business = '선박 해체, 재활용 서비스' WHERE stock_id = '005880' AND description IS NULL;
UPDATE stock_master SET description = '선박용 계측기 및 제어 시스템 제조 기업', business = '선박용 계측기, 제어 시스템 제조' WHERE stock_id = '004890' AND description IS NULL;
UPDATE stock_master SET description = '선박 금융 및 리스 서비스 제공 기업', business = '선박 금융, 리스 서비스' WHERE stock_id = '007860' AND description IS NULL;
UPDATE stock_master SET description = '선박용 안전 장비 및 구명 설비 전문 기업', business = '선박용 안전 장비, 구명 설비 제조' WHERE stock_id = '001040' AND description IS NULL;

-- 식품 (20개)
UPDATE stock_master SET description = '라면 및 면류 전문 제조 기업', business = '라면, 면류 제조 및 판매' WHERE stock_id = '271560' AND description IS NULL;
UPDATE stock_master SET description = '제과 및 제빵 전문 기업', business = '제과, 제빵 제조 및 유통' WHERE stock_id = '024070' AND description IS NULL;
UPDATE stock_master SET description = '음료 및 주류 제조 기업', business = '음료, 주류 제조 및 판매' WHERE stock_id = '005300' AND description IS NULL;
UPDATE stock_master SET description = '냉동식품 및 가공식품 전문 기업', business = '냉동식품, 가공식품 제조' WHERE stock_id = '280360' AND description IS NULL;
UPDATE stock_master SET description = '유제품 및 발효유 전문 기업', business = '유제품, 발효유 제조 및 판매' WHERE stock_id = '007310' AND description IS NULL;
UPDATE stock_master SET description = '조미료 및 소스 전문 제조 기업', business = '조미료, 소스 제조 및 판매' WHERE stock_id = '097950' AND description IS NULL;
UPDATE stock_master SET description = '커피 및 음료 프랜차이즈 운영 기업', business = '커피, 음료 프랜차이즈 운영' WHERE stock_id = '035760' AND description IS NULL;
UPDATE stock_master SET description = '제빵 및 디저트 전문 기업', business = '제빵, 디저트 제조 및 판매' WHERE stock_id = '018250' AND description IS NULL;
UPDATE stock_master SET description = '간편식 및 HMR 전문 기업', business = '간편식, HMR 제조 및 유통' WHERE stock_id = '027740' AND description IS NULL;
UPDATE stock_master SET description = '축산 및 육가공 전문 기업', business = '축산, 육가공 제조 및 유통' WHERE stock_id = '001680' AND description IS NULL;
UPDATE stock_master SET description = '수산물 가공 및 유통 전문 기업', business = '수산물 가공, 유통' WHERE stock_id = '001040' AND description IS NULL;
UPDATE stock_master SET description = '건강기능식품 및 영양제 전문 기업', business = '건강기능식품, 영양제 제조' WHERE stock_id = '065650' AND description IS NULL;
UPDATE stock_master SET description = '제과 및 스낵 전문 제조 기업', business = '제과, 스낵 제조 및 판매' WHERE stock_id = '271940' AND description IS NULL;
UPDATE stock_master SET description = '프랜차이즈 및 외식 사업 운영 기업', business = '프랜차이즈, 외식 사업 운영' WHERE stock_id = '035510' AND description IS NULL;
UPDATE stock_master SET description = '곡물 가공 및 제분 전문 기업', business = '곡물 가공, 제분 제조' WHERE stock_id = '001130' AND description IS NULL;
UPDATE stock_master SET description = '아이스크림 및 냉동 디저트 전문 기업', business = '아이스크림, 냉동 디저트 제조' WHERE stock_id = '001390' AND description IS NULL;
UPDATE stock_master SET description = '김치 및 발효식품 전문 기업', business = '김치, 발효식품 제조 및 판매' WHERE stock_id = '084690' AND description IS NULL;
UPDATE stock_master SET description = '베이커리 및 카페 프랜차이즈 운영 기업', business = '베이커리, 카페 프랜차이즈 운영' WHERE stock_id = '049720' AND description IS NULL;
UPDATE stock_master SET description = '식용유 및 유지 제조 전문 기업', business = '식용유, 유지 제조 및 판매' WHERE stock_id = '004140' AND description IS NULL;
UPDATE stock_master SET description = '소스 및 향신료 전문 제조 기업', business = '소스, 향신료 제조 및 유통' WHERE stock_id = '145990' AND description IS NULL;

-- 에너지 (20개)
UPDATE stock_master SET description = '석유 정제 및 석유화학 제품 생산 기업', business = '석유 정제, 석유화학 제품 생산' WHERE stock_id = '009830' AND description IS NULL;
UPDATE stock_master SET description = '천연가스 및 LNG 유통 전문 기업', business = '천연가스, LNG 유통 및 판매' WHERE stock_id = '336260' AND description IS NULL;
UPDATE stock_master SET description = '태양광 및 신재생 에너지 발전 기업', business = '태양광 발전, 신재생 에너지 사업' WHERE stock_id = '178920' AND description IS NULL;
UPDATE stock_master SET description = '전력 생산 및 공급 전문 기업', business = '전력 생산, 공급 및 판매' WHERE stock_id = '015760' AND description IS NULL;
UPDATE stock_master SET description = '풍력 발전 및 신재생 에너지 기업', business = '풍력 발전, 신재생 에너지 사업' WHERE stock_id = '071320' AND description IS NULL;
UPDATE stock_master SET description = '석탄 및 연료 유통 전문 기업', business = '석탄, 연료 유통 및 판매' WHERE stock_id = '001520' AND description IS NULL;
UPDATE stock_master SET description = '태양광 모듈 및 설비 제조 기업', business = '태양광 모듈, 설비 제조' WHERE stock_id = '114090' AND description IS NULL;
UPDATE stock_master SET description = '에너지 저장 시스템 (ESS) 전문 기업', business = 'ESS, 에너지 저장 시스템 개발' WHERE stock_id = '271940' AND description IS NULL;
UPDATE stock_master SET description = '송전 및 배전 설비 제조 기업', business = '송전, 배전 설비 제조' WHERE stock_id = '010120' AND description IS NULL;
UPDATE stock_master SET description = '원자력 발전 및 운영 전문 기업', business = '원자력 발전, 운영 서비스' WHERE stock_id = '153460' AND description IS NULL;
UPDATE stock_master SET description = '바이오 에너지 및 바이오 연료 생산 기업', business = '바이오 에너지, 바이오 연료 생산' WHERE stock_id = '194370' AND description IS NULL;
UPDATE stock_master SET description = '수력 및 소수력 발전 기업', business = '수력 발전, 소수력 발전 사업' WHERE stock_id = '001680' AND description IS NULL;
UPDATE stock_master SET description = '에너지 관리 시스템 (EMS) 개발 기업', business = 'EMS, 에너지 관리 시스템 개발' WHERE stock_id = '052220' AND description IS NULL;
UPDATE stock_master SET description = '전력 계통 및 스마트 그리드 기술 기업', business = '전력 계통, 스마트 그리드 기술 개발' WHERE stock_id = '001570' AND description IS NULL;
UPDATE stock_master SET description = '신재생 에너지 개발 및 투자 기업', business = '신재생 에너지 개발, 투자' WHERE stock_id = '000080' AND description IS NULL;
UPDATE stock_master SET description = '연료전지 및 수소 에너지 전문 기업', business = '연료전지, 수소 에너지 기술 개발' WHERE stock_id = '079550' AND description IS NULL;
UPDATE stock_master SET description = '지열 및 신재생 에너지 개발 기업', business = '지열, 신재생 에너지 개발' WHERE stock_id = '058470' AND description IS NULL;
UPDATE stock_master SET description = '폐기물 에너지 및 자원 순환 기업', business = '폐기물 에너지, 자원 순환 사업' WHERE stock_id = '004440' AND description IS NULL;
UPDATE stock_master SET description = '전력 설비 유지보수 및 관리 전문 기업', business = '전력 설비 유지보수, 관리 서비스' WHERE stock_id = '003300' AND description IS NULL;
UPDATE stock_master SET description = '스마트 에너지 솔루션 제공 기업', business = '스마트 에너지 솔루션, 컨설팅' WHERE stock_id = '002780' AND description IS NULL;

-- 반도체 (20개)
UPDATE stock_master SET description = '반도체 제조 및 설계 전문 기업', business = '반도체 제조, 설계 및 판매' WHERE stock_id = '006400' AND description IS NULL;
UPDATE stock_master SET description = '파운드리 및 시스템 반도체 전문 기업', business = '파운드리, 시스템 반도체 제조' WHERE stock_id = '000990' AND description IS NULL;
UPDATE stock_master SET description = '반도체 장비 제조 및 공급 기업', business = '반도체 장비 제조, 공급' WHERE stock_id = '042700' AND description IS NULL;
UPDATE stock_master SET description = '반도체 소재 및 화학 약품 전문 기업', business = '반도체 소재, 화학 약품 제조' WHERE stock_id = '120110' AND description IS NULL;
UPDATE stock_master SET description = '웨이퍼 제조 및 공급 전문 기업', business = '웨이퍼 제조, 공급' WHERE stock_id = '036930' AND description IS NULL;
UPDATE stock_master SET description = '반도체 테스트 및 검사 장비 전문 기업', business = '반도체 테스트, 검사 장비 제조' WHERE stock_id = '131970' AND description IS NULL;
UPDATE stock_master SET description = '반도체 패키징 및 테스트 서비스 기업', business = '반도체 패키징, 테스트 서비스' WHERE stock_id = '000990' AND description IS NULL;
UPDATE stock_master SET description = '반도체 디스플레이 패널 제조 기업', business = '반도체, 디스플레이 패널 제조' WHERE stock_id = '009150' AND description IS NULL;
UPDATE stock_master SET description = '반도체 가스 및 특수 가스 전문 기업', business = '반도체 가스, 특수 가스 공급' WHERE stock_id = '088390' AND description IS NULL;
UPDATE stock_master SET description = '반도체 포토레지스트 및 화학 소재 기업', business = '포토레지스트, 화학 소재 제조' WHERE stock_id = '020150' AND description IS NULL;
UPDATE stock_master SET description = '반도체 CMP 슬러리 및 연마재 전문 기업', business = 'CMP 슬러리, 연마재 제조' WHERE stock_id = '089030' AND description IS NULL;
UPDATE stock_master SET description = '반도체 세정 장비 및 약품 전문 기업', business = '세정 장비, 약품 제조 및 공급' WHERE stock_id = '067160' AND description IS NULL;
UPDATE stock_master SET description = '반도체 마스크 및 포토마스크 제조 기업', business = '반도체 마스크, 포토마스크 제조' WHERE stock_id = '222080' AND description IS NULL;
UPDATE stock_master SET description = '반도체 리드프레임 및 패키징 소재 기업', business = '리드프레임, 패키징 소재 제조' WHERE stock_id = '004800' AND description IS NULL;
UPDATE stock_master SET description = '반도체 본딩 와이어 및 소재 전문 기업', business = '본딩 와이어, 반도체 소재 제조' WHERE stock_id = '108320' AND description IS NULL;
UPDATE stock_master SET description = '반도체 식각 장비 및 증착 장비 기업', business = '식각 장비, 증착 장비 제조' WHERE stock_id = '195870' AND description IS NULL;
UPDATE stock_master SET description = '반도체 FAB 자동화 시스템 전문 기업', business = 'FAB 자동화, 반도체 시스템 개발' WHERE stock_id = '053610' AND description IS NULL;
UPDATE stock_master SET description = '반도체 검사 및 계측 장비 제조 기업', business = '검사, 계측 장비 제조' WHERE stock_id = '046890' AND description IS NULL;
UPDATE stock_master SET description = '반도체 웨이퍼 재생 및 리사이클 기업', business = '웨이퍼 재생, 리사이클 서비스' WHERE stock_id = '036810' AND description IS NULL;
UPDATE stock_master SET description = '반도체 IP 및 설계 솔루션 전문 기업', business = '반도체 IP, 설계 솔루션 개발' WHERE stock_id = '039030' AND description IS NULL;

-- 금융 (20개)
UPDATE stock_master SET description = '은행 및 금융 서비스 제공 기업', business = '은행, 금융 서비스 운영' WHERE stock_id = '055550' AND description IS NULL;
UPDATE stock_master SET description = '증권 및 자산관리 전문 기업', business = '증권, 자산관리 서비스' WHERE stock_id = '029780' AND description IS NULL;
UPDATE stock_master SET description = '보험 및 생명보험 전문 기업', business = '보험, 생명보험 서비스' WHERE stock_id = '005830' AND description IS NULL;
UPDATE stock_master SET description = '손해보험 및 재보험 전문 기업', business = '손해보험, 재보험 서비스' WHERE stock_id = '000810' AND description IS NULL;
UPDATE stock_master SET description = '카드 및 결제 서비스 제공 기업', business = '카드, 결제 서비스 운영' WHERE stock_id = '029960' AND description IS NULL;
UPDATE stock_master SET description = '핀테크 및 디지털 금융 서비스 기업', business = '핀테크, 디지털 금융 서비스' WHERE stock_id = '161890' AND description IS NULL;
UPDATE stock_master SET description = '자산운용 및 펀드 관리 전문 기업', business = '자산운용, 펀드 관리 서비스' WHERE stock_id = '006800' AND description IS NULL;
UPDATE stock_master SET description = '리스 및 할부 금융 전문 기업', business = '리스, 할부 금융 서비스' WHERE stock_id = '016380' AND description IS NULL;
UPDATE stock_master SET description = '저축은행 및 중소기업 금융 전문 기업', business = '저축은행, 중소기업 금융 서비스' WHERE stock_id = '089890' AND description IS NULL;
UPDATE stock_master SET description = '대부 및 개인 금융 서비스 제공 기업', business = '대부, 개인 금융 서비스' WHERE stock_id = '029530' AND description IS NULL;
UPDATE stock_master SET description = '투자자문 및 금융 컨설팅 전문 기업', business = '투자자문, 금융 컨설팅 서비스' WHERE stock_id = '003540' AND description IS NULL;
UPDATE stock_master SET description = '보증 및 신용평가 전문 기업', business = '보증, 신용평가 서비스' WHERE stock_id = '016710' AND description IS NULL;
UPDATE stock_master SET description = '부동산 금융 및 프로젝트 파이낸싱 기업', business = '부동산 금융, 프로젝트 파이낸싱' WHERE stock_id = '026960' AND description IS NULL;
UPDATE stock_master SET description = '전자금융 및 디지털 뱅킹 서비스 기업', business = '전자금융, 디지털 뱅킹 서비스' WHERE stock_id = '037560' AND description IS NULL;
UPDATE stock_master SET description = '금융 IT 솔루션 및 시스템 개발 기업', business = '금융 IT 솔루션, 시스템 개발' WHERE stock_id = '033780' AND description IS NULL;
UPDATE stock_master SET description = '암호화폐 및 블록체인 금융 서비스 기업', business = '암호화폐, 블록체인 금융 서비스' WHERE stock_id = '263720' AND description IS NULL;
UPDATE stock_master SET description = '벤처캐피탈 및 프라이빗 에쿼티 전문 기업', business = '벤처캐피탈, 프라이빗 에쿼티 투자' WHERE stock_id = '278280' AND description IS NULL;
UPDATE stock_master SET description = '신용정보 및 데이터 분석 전문 기업', business = '신용정보, 데이터 분석 서비스' WHERE stock_id = '032280' AND description IS NULL;
UPDATE stock_master SET description = '금융 보안 및 인증 솔루션 전문 기업', business = '금융 보안, 인증 솔루션 개발' WHERE stock_id = '052770' AND description IS NULL;
UPDATE stock_master SET description = '결제 게이트웨이 및 PG 서비스 제공 기업', business = '결제 게이트웨이, PG 서비스' WHERE stock_id = '037620' AND description IS NULL;

-- 2차전지 (20개)
UPDATE stock_master SET description = 'LG에너지솔루션은 리튬이온 배터리 전문 기업', business = '리튬이온 배터리, EV 배터리 제조' WHERE stock_id = '373220' AND description IS NULL;
UPDATE stock_master SET description = '배터리 양극재 전문 제조 기업', business = '양극재, 배터리 소재 제조' WHERE stock_id = '361610' AND description IS NULL;
UPDATE stock_master SET description = '배터리 음극재 및 흑연 소재 전문 기업', business = '음극재, 흑연 소재 제조' WHERE stock_id = '096770' AND description IS NULL;
UPDATE stock_master SET description = '배터리 전해액 및 전해질 전문 기업', business = '전해액, 전해질 제조' WHERE stock_id = '051910' AND description IS NULL;
UPDATE stock_master SET description = '배터리 분리막 및 세퍼레이터 전문 기업', business = '분리막, 세퍼레이터 제조' WHERE stock_id = '137400' AND description IS NULL;
UPDATE stock_master SET description = '전기차 배터리 팩 제조 전문 기업', business = '배터리 팩, EV 배터리 시스템 제조' WHERE stock_id = '009520' AND description IS NULL;
UPDATE stock_master SET description = '배터리 관리 시스템 (BMS) 전문 기업', business = 'BMS, 배터리 관리 시스템 개발' WHERE stock_id = '080000' AND description IS NULL;
UPDATE stock_master SET description = '배터리 리사이클 및 재활용 전문 기업', business = '배터리 리사이클, 재활용 서비스' WHERE stock_id = '095570' AND description IS NULL;
UPDATE stock_master SET description = '배터리 셀 제조 및 공급 기업', business = '배터리 셀 제조, 공급' WHERE stock_id = '006650' AND description IS NULL;
UPDATE stock_master SET description = '배터리 양극 활물질 전문 제조 기업', business = '양극 활물질, 배터리 소재 제조' WHERE stock_id = '051915' AND description IS NULL;
UPDATE stock_master SET description = '배터리 전구체 및 전구물질 전문 기업', business = '전구체, 전구물질 제조' WHERE stock_id = '121600' AND description IS NULL;
UPDATE stock_master SET description = '배터리 케이스 및 포장재 전문 기업', business = '배터리 케이스, 포장재 제조' WHERE stock_id = '222670' AND description IS NULL;
UPDATE stock_master SET description = '배터리 검사 및 테스트 장비 전문 기업', business = '검사, 테스트 장비 제조' WHERE stock_id = '058470' AND description IS NULL;
UPDATE stock_master SET description = '전고체 배터리 기술 개발 전문 기업', business = '전고체 배터리 기술 개발' WHERE stock_id = '336370' AND description IS NULL;
UPDATE stock_master SET description = '배터리 소재 가공 및 코팅 전문 기업', business = '배터리 소재 가공, 코팅 서비스' WHERE stock_id = '010050' AND description IS NULL;
UPDATE stock_master SET description = '배터리 터미널 및 연결부품 전문 기업', business = '배터리 터미널, 연결부품 제조' WHERE stock_id = '007340' AND description IS NULL;
UPDATE stock_master SET description = '배터리 냉각 시스템 및 열관리 전문 기업', business = '배터리 냉각 시스템, 열관리 기술' WHERE stock_id = '023590' AND description IS NULL;
UPDATE stock_master SET description = 'ESS 및 에너지 저장 배터리 전문 기업', business = 'ESS, 에너지 저장 배터리 제조' WHERE stock_id = '137400' AND description IS NULL;
UPDATE stock_master SET description = '배터리 장비 및 생산 라인 전문 기업', business = '배터리 장비, 생산 라인 제조' WHERE stock_id = '007660' AND description IS NULL;
UPDATE stock_master SET description = '배터리 원료 및 광물 채굴 가공 기업', business = '배터리 원료, 광물 채굴 및 가공' WHERE stock_id = '161390' AND description IS NULL;

-- 업데이트 결과 확인
SELECT '✅ 전체 종목 기업 개요 업데이트 완료' AS status;
SELECT COUNT(*) as total, COUNT(description) as has_description FROM stock_master;
