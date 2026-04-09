# TriAheads

TriAheads は、音声入力を使って素早くメモを残せる Android アプリです。  
未ログイン時はローカル（Room）に保存し、Google ログイン後は Firebase Firestore に保存先を切り替えます。

## 主な機能

- 音声入力（`RecognizerIntent`）でメモを追加
- メモ一覧表示（新しい更新順）
- メモの編集・削除
- メモ共有（他アプリへテキスト送信）
- プル・トゥ・リフレッシュで再読み込み
- 壁紙画像の変更（端末内ファイルへ保存）
- Google ログイン / ログアウト（Firebase Auth）

## 技術スタック

- Kotlin / Jetpack Compose / Material 3
- Navigation Compose
- Room
- Hilt
- Firebase Authentication / Cloud Firestore
- Google Sign-In（Play Services Auth）
- Coil

## 動作要件

- Android Studio（最新安定版推奨）
- JDK 11
- Android SDK
  - `minSdk = 24`
  - `targetSdk = 36`
  - `compileSdk = 37`

## セットアップ

1. プロジェクトを開く
2. `local.properties` に Android SDK パスを設定
3. Firebase を使う場合は以下を追加
   - Firebase プロジェクトを作成
   - Android アプリ（`com.abplus.triaheads`）を登録
   - `google-services.json` を `app/` 配下に配置
   - Google サインインを有効化
4. Android Studio から実行、または以下でビルド

```bash
./gradlew assembleDebug
```

## 使い方

1. 画面右下の `+` ボタンを押して音声入力し、メモを追加
2. 一覧カードの操作から共有・編集・削除
3. 右上メニューから壁紙変更、ログイン/ログアウト

## データ保存の挙動

- 未ログイン: Room（端末ローカルDB）
- ログイン中: Firestore（`users/{uid}/notes`）
- 認証状態の変化に応じてリポジトリを自動切り替え

## テスト

```bash
./gradlew test
./gradlew connectedAndroidTest
```

## ライセンス

このプロジェクトは [MIT License](LICENSE) の下で公開されています。
