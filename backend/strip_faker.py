from pathlib import Path
import re
import shutil

root = Path(__file__).resolve().parent
changelog = root / "src/main/resources/config/liquibase/changelog"
fake_data = root / "src/main/resources/config/liquibase/fake-data"

pattern = re.compile(
    r"\n    <!--\n        Load sample data generated with Faker\.js.*?    </changeSet>\n",
    re.S,
)

for xml in changelog.glob("*.xml"):
    text = xml.read_text(encoding="utf-8")
    updated = pattern.sub("\n", text)
    if updated != text:
        xml.write_text(updated, encoding="utf-8")
        print(f"stripped {xml.name}")

if fake_data.exists():
    shutil.rmtree(fake_data)
    print("removed fake-data directory")
