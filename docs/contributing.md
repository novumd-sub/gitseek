# チーム開発ガイドライン

<!--toc:start-->
- [チーム開発ガイドライン](#チーム開発ガイドライン)
  - [Gitの運用ルール](#gitの運用ルール)
    - [コミットメッセージテンプレート](#コミットメッセージテンプレート)
  - [静的解析](#静的解析)
    - [セットアップ手順](#セットアップ手順)
<!--toc:end-->

## Gitの運用ルール

ブランチおよびコミットメッセージのプレフィックスに「作業意図(用途: Usage)の明確化」のため、4つのシンプルなメッセージを付け加える

- `feat`:
  - 機能の追加
- `fix`:
  - バグ・仕様指摘の修正
- `refactor`:
  - コード構造・パフォーマンスの改善
- `chore`:
  - プロジェクト内の雑用(ドキュメント更新、依存関係追加・更新..etc)

### コミットメッセージテンプレート

LazyGitのカスタムコマンド例:

```yaml
customCommands:
  - command: git commit -m '{{ .Form.commit_usage }} {{ .Form.commit_message }}'
    context: files
    description: Commit changes using gitmojis
    key: C
    prompts:
      - key: commit_usage
        type: menu
        title: 'Select commit usage:'
        filter: ^(.*?) - (:.*?:) - (.*)$
        options:
          - name: '✨ feat:'
            description: "機能追加・リリース"
            value: '✨ feat: '
          - name: "\U0001F41B fix:"
            description: "バグ・指摘修正"
            value: "\U0001F41B fix: "
          - name: '♻️ refactor:'
            description: "コード・パフォーマンス改善"
            value: '♻️ refactor: '
          - name: "\U0001F3D7️ chore:"
            description: "プロジェクト内雑用(ドキュメント・ライブラリ更新...etc)"
            value: "\U0001F3D7️ chore: "
          - name: "\U0001F4DA learn:"
            description: "個人学習"
            value: "\U0001F4DA learn: "
      - key: commit_message
        type: input
        title: 'Enter a commit message:'

```

> [!TIP]  
> **コミットメッセージのテンプレート**
>
> 使用するツールは問わず、以下のようなコミットメッセージのテンプレートを用意しておくと便利です
>
>
>`commit_template.txt`
>
>```
># ✨ feat:
># 🐛 fix:
># ♻️ refactor:
># 🏗️ chore:
>```
>
>
>`.gitconfig`
>
>```
>[commit]
>template = ./[path]/commit_message_template.txt
>```
>
>`.gitconfig`の`commit.template`を設定しておくと、`git commit`時にテンプレートとして呼び出せる

## 静的解析

detekt cli + ktlint formattingプラグイン + twitter-compose-rulesプラグイン で静的解析とコード整形を行います。

pre-commit時にステージングエリアの変更を対象にリント・フォーマットを実行し、HTMLレポートを生成し、ブラウザ表示します。HTMLレポートにあるリンクから公式のルール説明ページに飛べるため、リント・フォーマットルールの確認が容易です。

### セットアップ手順

まず、[detekt cli](https://detekt.dev/docs/1.23.7/gettingstarted/cli)をインストールします。

インストールが完了したら、 プロジェクトルートに以下のような静的解析ツールの設定ファイル・プラグインを配置します。

```sh
tre                                                                                                                          Wed Dec 17 02:38:47 2025
.
├── baseline.xml
├── config.yaml
├── formatting-config.yaml
├── plugins
│   ├── detekt-formatting-1.23.7.jar
│   └── detekt-twitter-compose-0.0.26-all.jar
├── pre-commit
└── reports
    └── log.html
```

プロジェクトの直下に、上記のhooksディレクトリがあるので、プロジェクトの.gitディレクトリ上にコピーします。

```sh
cp -rf ./hooks ./.git/
```

後は、通常通りにコミットを行うと、ステージングエリアの変更を対象に静的解析が実行され、レポートが生成されます。

```
git add .
git commit
```
