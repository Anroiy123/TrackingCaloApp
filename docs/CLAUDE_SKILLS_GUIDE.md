# Hướng Dẫn Sử Dụng Claude Code Skills

Tài liệu này hướng dẫn cách sử dụng các Skills của Claude Code một cách hiệu quả cho dự án TrackingCaloApp.

---

## Mục Lục

1. [Giới Thiệu Skills](#giới-thiệu-skills)
2. [Skills Phù Hợp Với Project](#skills-phù-hợp-với-project)
3. [Slash Commands Thường Dùng](#slash-commands-thường-dùng)
4. [Hướng Dẫn Sử Dụng Chi Tiết](#hướng-dẫn-sử-dụng-chi-tiết)
5. [Ví Dụ Thực Tế](#ví-dụ-thực-tế)
6. [Best Practices](#best-practices)

---

## Giới Thiệu Skills

**Skills** là các khả năng chuyên biệt của Claude Code, được thiết kế để xử lý các tác vụ cụ thể một cách hiệu quả. Mỗi skill chứa:
- Kiến thức chuyên sâu về một lĩnh vực
- Workflows tối ưu
- Best practices và patterns

**Cách kích hoạt Skill:**
```
skill: "tên-skill"
```

Hoặc sử dụng **Slash Commands** (bắt đầu bằng `/`):
```
/command [tham-số]
```

---

## Skills Phù Hợp Với Project

### 1. UI/UX & Design

#### `frontend-design`
Tạo giao diện người dùng đẹp và production-ready.

**Khi nào sử dụng:**
- Thiết kế màn hình mới
- Cải thiện UI hiện có
- Tạo components Material Design 3

**Ví dụ:**
```
skill: "frontend-design"

Thiết kế lại màn hình DiaryActivity với:
- Card view cho mỗi food entry
- Animation khi swipe delete
- Floating action button đẹp hơn
```

#### `aesthetic`
Áp dụng nguyên tắc thẩm mỹ vào thiết kế.

**Khi nào sử dụng:**
- Cần UI đẹp theo chuẩn design principles
- Phân tích và cải thiện visual hierarchy
- Thêm micro-interactions

**Ví dụ:**
```
skill: "aesthetic"

Phân tích màn hình MainActivity và đề xuất cải thiện:
- Color harmony
- Typography scale
- Visual balance
```

---

### 2. Database & Backend

#### `databases`
Làm việc với cơ sở dữ liệu (Room/SQLite).

**Khi nào sử dụng:**
- Thiết kế schema mới
- Tối ưu queries
- Debug vấn đề database
- Viết migrations

**Ví dụ:**
```
skill: "databases"

Tối ưu query getTotalCaloriesByDate():
- Hiện tại đang chậm với nhiều entries
- Cần thêm index phù hợp
- Xem xét caching strategy
```

#### `backend-development`
Phát triển logic backend và APIs.

**Khi nào sử dụng:**
- Thiết kế repository pattern
- Implement business logic
- Viết unit tests
- Xử lý authentication (nếu có)

**Ví dụ:**
```
skill: "backend-development"

Thiết kế sync feature để backup dữ liệu:
- Local Room database
- Sync lên cloud (Firebase/custom API)
- Conflict resolution strategy
```

---

### 3. Debugging & Quality

#### `debugging`
Framework debugging có hệ thống, tìm root cause.

**Khi nào sử dụng:**
- App crash không rõ nguyên nhân
- Logic sai nhưng không biết ở đâu
- Performance issues
- Memory leaks

**Ví dụ:**
```
skill: "debugging"

App crash khi thêm food entry:
- Xảy ra ngẫu nhiên
- Error log: NullPointerException
- Liên quan đến database write
```

#### `code-review`
Review code đảm bảo chất lượng.

**Khi nào sử dụng:**
- Sau khi implement feature mới
- Trước khi merge code
- Kiểm tra security vulnerabilities
- Đánh giá code quality

**Ví dụ:**
```
skill: "code-review"

Review các thay đổi trong AddFoodActivity:
- Kiểm tra memory leaks
- Đánh giá error handling
- Verify thread safety với Room
```

---

### 4. Planning & Problem Solving

#### `planning`
Lập kế hoạch technical solutions.

**Khi nào sử dụng:**
- Bắt đầu feature lớn
- Cần architectural decisions
- Đánh giá trade-offs

**Ví dụ:**
```
skill: "planning"

Lập kế hoạch thêm tính năng:
- Barcode scanner để thêm food
- Offline-first approach
- Tích hợp với Open Food Facts API
```

#### `sequential-thinking`
Giải quyết vấn đề phức tạp theo bước.

**Khi nào sử dụng:**
- Vấn đề có nhiều bước
- Cần phân tích kỹ trước khi code
- Logic phức tạp cần chia nhỏ

**Ví dụ:**
```
skill: "sequential-thinking"

Phân tích logic tính TDEE:
- Các biến đầu vào
- Công thức Mifflin-St Jeor
- Activity multiplier
- Weight goal adjustment
- Edge cases cần xử lý
```

#### `problem-solving`
Kỹ thuật giải quyết vấn đề đặc biệt.

**Khi nào sử dụng:**
- Bị stuck, không biết hướng đi
- Cần suy nghĩ sáng tạo
- Vấn đề recurring cần meta-pattern

---

### 5. Documentation & Research

#### `docs-seeker`
Tìm kiếm documentation kỹ thuật.

**Khi nào sử dụng:**
- Cần docs của thư viện
- Tìm hiểu API mới
- Research best practices

**Ví dụ:**
```
skill: "docs-seeker"

Tìm documentation về:
- Room Database migrations
- Material Design 3 components
- ViewPager2 với Fragments
```

#### `repomix`
Đóng gói codebase để phân tích.

**Khi nào sử dụng:**
- Cần overview toàn bộ project
- Phân tích cấu trúc code
- Chuẩn bị context cho AI analysis

---

## Slash Commands Thường Dùng

### Development Workflow

| Command | Mô tả | Ví dụ |
|---------|-------|-------|
| `/plan [task]` | Lập kế hoạch implementation | `/plan thêm tính năng export CSV` |
| `/plan:fast [task]` | Lập kế hoạch nhanh (không research) | `/plan:fast fix bug null pointer` |
| `/cook [tasks]` | Implement feature step by step | `/cook thêm dark mode` |
| `/cook:auto [tasks]` | Implement tự động | `/cook:auto refactor DiaryActivity` |

### Debugging & Fixing

| Command | Mô tả | Ví dụ |
|---------|-------|-------|
| `/fix [issues]` | Sửa bugs (auto detect complexity) | `/fix app crash khi xóa entry` |
| `/fix:fast [issues]` | Sửa nhanh issues đơn giản | `/fix:fast typo trong strings.xml` |
| `/fix:hard [issues]` | Sửa issues phức tạp với subagents | `/fix:hard memory leak trong adapter` |
| `/debug [issues]` | Debug vấn đề kỹ thuật | `/debug tại sao LiveData không update` |
| `/test` | Chạy tests và phân tích kết quả | `/test` |

### Design & UI

| Command | Mô tả | Ví dụ |
|---------|-------|-------|
| `/design:fast [tasks]` | Thiết kế UI nhanh | `/design:fast card cho food entry` |
| `/design:good [tasks]` | Thiết kế UI immersive | `/design:good onboarding flow mới` |
| `/design:screenshot [path]` | Thiết kế từ screenshot | `/design:screenshot docs/screenshots/main.png` |

### Git & Version Control

| Command | Mô tả | Ví dụ |
|---------|-------|-------|
| `/git:cm` | Stage all và commit | `/git:cm` |
| `/git:cp` | Stage, commit và push | `/git:cp` |
| `/git:pr [branch]` | Tạo pull request | `/git:pr feature/dark-mode` |

### Documentation

| Command | Mô tả | Ví dụ |
|---------|-------|-------|
| `/docs:init` | Tạo documentation ban đầu | `/docs:init` |
| `/docs:update` | Cập nhật docs theo code | `/docs:update` |

### Code Analysis

| Command | Mô tả | Ví dụ |
|---------|-------|-------|
| `/scout [prompt]` | Tìm kiếm files liên quan | `/scout files xử lý calorie calculation` |
| `/review:codebase [prompt]` | Phân tích codebase | `/review:codebase security vulnerabilities` |
| `/ask [question]` | Hỏi technical questions | `/ask best practice cho Room migrations` |

---

## Hướng Dẫn Sử Dụng Chi Tiết

### Workflow 1: Thêm Feature Mới

```
Bước 1: Lập kế hoạch
/plan thêm tính năng water tracking

Bước 2: Review kế hoạch và xác nhận

Bước 3: Implement
/cook implement water tracking theo plan

Bước 4: Test
/test

Bước 5: Review code
skill: "code-review"
Review water tracking feature vừa implement

Bước 6: Commit
/git:cm
```

### Workflow 2: Fix Bug

```
Bước 1: Debug
/debug app crash khi thêm food với quantity = 0

Bước 2: Fix
/fix validation cho quantity input

Bước 3: Test
/test

Bước 4: Commit
/git:cm
```

### Workflow 3: Cải Thiện UI

```
Bước 1: Phân tích
skill: "aesthetic"
Phân tích UI của ProfileActivity

Bước 2: Thiết kế
/design:good cải thiện ProfileActivity theo đề xuất

Bước 3: Review
Xem preview và adjust

Bước 4: Commit
/git:cm
```

---

## Ví Dụ Thực Tế

### Ví dụ 1: Thêm Food Entry Validation

**Yêu cầu:** Validate input trước khi thêm food entry

```
/plan thêm validation cho AddFoodActivity:
- Quantity phải > 0 và <= 10000
- Hiển thị error message bằng tiếng Việt
- Disable button khi invalid
```

**Sau khi có plan:**
```
/cook implement validation theo plan
```

### Ví dụ 2: Debug Database Issue

**Yêu cầu:** Food entries không hiển thị sau khi thêm

```
/debug food entries không appear trong DiaryFragment:
- Đã thêm thành công (không có error)
- Reload app thì thấy
- LiveData có vấn đề?
```

### Ví dụ 3: Optimize Query Performance

**Yêu cầu:** Query chậm với nhiều entries

```
skill: "databases"

Phân tích và tối ưu performance cho:
- FoodEntryDao.getEntriesByDateRange()
- Hiện có ~1000 entries
- Query mất 2-3 giây

Database schema:
- food_entries table với date timestamp
- Foreign key đến foods table
```

### Ví dụ 4: Thiết Kế Màn Hình Statistics

**Yêu cầu:** Thêm màn hình thống kê weekly/monthly

```
/design:good màn hình Statistics:
- Chart hiển thị calories theo tuần/tháng
- So sánh consumed vs burned
- Trend line cho weight progress
- Material Design 3 style
- Màu sắc theo theme của app (green primary)
```

### Ví dụ 5: Research Thư Viện Mới

**Yêu cầu:** Tìm thư viện chart phù hợp

```
skill: "docs-seeker"

Tìm và so sánh các chart libraries cho Android:
- MPAndroidChart
- AAChartCore
- Charts by PhilJay

Tiêu chí:
- Hỗ trợ line, bar, pie charts
- Customizable colors
- Performance với nhiều data points
- Dễ integrate với Room/LiveData
```

---

## Best Practices

### 1. Chọn Đúng Tool

| Tình huống | Nên dùng |
|------------|----------|
| Bug đơn giản, rõ nguyên nhân | `/fix:fast` |
| Bug phức tạp, cần investigation | `/debug` rồi `/fix` |
| Feature nhỏ, đã biết cách làm | `/cook:auto` |
| Feature lớn, cần planning | `/plan` rồi `/cook` |
| Cải thiện UI nhẹ | `/design:fast` |
| Redesign màn hình | `/design:good` |

### 2. Cung Cấp Context Đầy Đủ

**Không tốt:**
```
/fix app crash
```

**Tốt:**
```
/fix app crash trong AddFoodActivity:
- Xảy ra khi nhấn nút "Thêm"
- Quantity = 0
- Error: ArithmeticException divide by zero
- File: AddFoodActivity.java:156
```

### 3. Chia Nhỏ Tasks

**Không tốt:**
```
/cook thêm tính năng sync, backup, restore, và export
```

**Tốt:**
```
/plan tính năng data management (sync, backup, restore, export)
# Review plan, chia thành phases

/cook implement Phase 1: Local backup/restore
# Test và commit

/cook implement Phase 2: Cloud sync
# Test và commit
```

### 4. Review Trước Khi Commit

```
# Sau khi implement
skill: "code-review"
Review changes trong AddFoodActivity

# Nếu OK
/git:cm
```

### 5. Document Khi Cần

```
# Sau khi thêm feature phức tạp
/docs:update

# Hoặc cụ thể hơn
Cập nhật docs/API.md với endpoint mới
```

---

## Tóm Tắt Quick Reference

### Skills Hay Dùng Nhất

1. **`debugging`** - Debug issues
2. **`code-review`** - Review code quality
3. **`planning`** - Lập kế hoạch features
4. **`databases`** - Làm việc với Room/SQLite
5. **`frontend-design`** - Thiết kế UI

### Commands Hay Dùng Nhất

1. **`/fix [issue]`** - Sửa bugs
2. **`/cook [task]`** - Implement features
3. **`/plan [task]`** - Lập kế hoạch
4. **`/test`** - Chạy tests
5. **`/git:cm`** - Commit changes

### Kết Hợp Hiệu Quả

```
/plan → /cook → /test → code-review → /git:cm
```

---

## Liên Hệ & Hỗ Trợ

- **Claude Code Help:** `/help`
- **Feedback:** https://github.com/anthropics/claude-code/issues
- **Project Docs:** Xem thêm trong thư mục `docs/`

---

*Tài liệu này được tạo cho dự án TrackingCaloApp - Vietnamese Calorie Tracking Application*
