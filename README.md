# Git workflow
### Trunk Based Development

## 1. Workflow
### 2.1. Working branch
Working branch sẽ tương tự [trunk-based development](https://github.com/stakater/tbd-cd-workflow) - chỉ sử dụng 1 branch duy nhất để phát triển là `develop`.

Project có những nhánh sau:
- nhánh `master` hiện tại chỉ để lưu trữ và build code.
- nhánh `develop` là nhánh chính mà các dev sẽ tích hợp code thường xuyên.
- nhánh `staging` là nhánh code trên server test.
- nhánh `product` là nhánh code trên server product.

```
Chú ý: dev cần chia nhỏ task và tích hợp code thường xuyên.
```

### 2.2. Code review & integration
#### 2.2.1. Code development
- Khi dev làm một tính năng hay sửa một bug thì sẽ checkout về nhánh `dev`, pull code về
- Sau khi lấy được code mới nhất trên `develop` thì bắt đầu thực hiện việc tách từ nhánh `develop` ra theo convention dưới đây
- Checkout đến nhánh vừa tạo và bắt đầu code trên đấy. Đối với mỗi 1 nhánh sẽ chỉ có 1 commit thì khi tạo merge request thì tên của merge request sẽ lấy từ tên commit luôn. 

```
Lưu ý: Trong khi code nên thường xuyên rebase nhánh 
```

#### Convention khi tạo nhánh
```
<type>/<scope>
```

Ví dụ: `feat/auth-login`

Dưới đây là một số type được định nghĩa sẵn:
- feat: A new feature.
- fix: A bug fix.
- refactor: A code change that neither fixes a bug nor adds a feature.
- build: Changes that affect the build system or external dependencies.
- ci: Changes to our CI configuration files and scripts.
- docs: Documentation only changes.
- perf: A code change that improves performance.
- test: Adding missing tests or correcting existing tests.

#### 2.2.2. Code integration
Project này sử dụng git rebase cho việc tích hợp code. **Để có thể rebase dễ dàng thì dev cần tích hợp code thường xuyên**.

```bash
# Ví dụ: chuỗi câu lệnh mà dev sẽ thường thực hiện khi tích hợp code.
git status
git add .
git commit -m 'feat(scope): work description (Task ID)'
git pull --rebase origin develop
git push origin feat/xxx
```

```
Lưu ý: nhánh develop, master là nhánh public nên không được push sửa vào
```

#### 2.2.3. Code review
- Developer sau khi code xong trên nhánh tương ứng với việc phải làm thì sẽ tạo request merge đến nhánh `develop` trên git
- Frontend leader sẽ kiểm tra các merge request rồi note lại những thứ không phù hợp rồi báo với dev để sửa (Có thể note trên gitlab, jira hoặc trao đổi trực tiếp với dev)
- Nếu code có vấn đề thì dev sẽ phải sửa lại rồi tạo lại merge request và phải review lại
- Nếu không có vấn đề thì Frontend Leader sẽ chấp nhận merge request và xoá nhánh cũ đi

### 2.3. Release
- Frontend leader sẽ xem merge nhánh `develop` vào nhánh `staging` để triển khai lên server test tuỳ theo thời điểm cho tester test
- Sau khi tester test ok thì sẽ merge tiếp nhánh `staging` vào nhánh `product` để triển khai lên server product
- Có thể sẽ cần việc đánh phiên bản dùng tag của git cho mỗi lần triển khai lên server product

## 3. Commit convention
```
<type>(<scope - sub scope>): <subject> (Task ID)
<BLANK LINE>
<body>
<BLANK LINE>
<footer>
```

Ví dụ: `feat(auth - login): add social login button (TV-10)`

Dưới đây là một số type được định nghĩa sẵn:
- build: Changes that affect the build system or external dependencies.
- ci: Changes to our CI configuration files and scripts.
- docs: Documentation only changes.
- feat: A new feature.
- fix: A bug fix.
- perf: A code change that improves performance.
- refactor: A code change that neither fixes a bug nor adds a feature.
- test: Adding missing tests or correcting existing tests.

Lưu ý: Khi làm dự án sử dụng Jira thì phải thêm Task ID vào commit message để có thể track được commit

Tham khảo: https://github.com/angular/angular/blob/master/CONTRIBUTING.md#-commit-message-format

## 4. Merge request
Khi tạo merge request tại phần `Description` ta sẽ chọn template `default` để merge request tự tạo ra checklist

- Điền ticket link, ticket number
- Điền mô tả của sự thay đổi lần này
- Thêm screenshot hoặc quay màn hình của tính năng mới hoặc bug vừa sửa
- Kiểm tra các lựa chọn trong checklist xem đã đảm bảo chưa và tick vào
