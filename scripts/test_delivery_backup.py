import hashlib
import importlib.util
import json
from pathlib import Path
import tempfile
import unittest
import zipfile

spec = importlib.util.spec_from_file_location('delivery_backup', Path(__file__).with_name('delivery_backup.py'))
backup = importlib.util.module_from_spec(spec)
spec.loader.exec_module(backup)


class ArchiveTests(unittest.TestCase):
    def write(self, root, files, hashes=None):
        archive = root / 'backup.zip'
        manifest = {'format': 1, 'files': hashes or {
            name: hashlib.sha256(value).hexdigest() for name, value in files.items()}}
        with zipfile.ZipFile(archive, 'w') as out:
            for name, value in files.items():
                out.writestr(name, value)
            out.writestr('manifest.json', json.dumps(manifest))
        return archive

    def test_roundtrip_database_and_images(self):
        with tempfile.TemporaryDirectory() as directory:
            root = Path(directory)
            source = self.write(root, {'database.sql': b'SELECT 1;', 'material/2026/logo.png': b'png'})
            target = root / 'restored'
            backup.verify_archive(source, target)
            self.assertEqual((target / 'material/2026/logo.png').read_bytes(), b'png')

    def test_tampered_content_rejected(self):
        with tempfile.TemporaryDirectory() as directory:
            root = Path(directory)
            source = self.write(root, {'database.sql': b'changed'}, {'database.sql': '0' * 64})
            with self.assertRaises(ValueError):
                backup.verify_archive(source, root / 'restored')

    def test_path_traversal_rejected(self):
        with tempfile.TemporaryDirectory() as directory:
            root = Path(directory)
            source = self.write(root, {'database.sql': b'sql', 'material/../../escape': b'bad'})
            with self.assertRaises(ValueError):
                backup.verify_archive(source, root / 'restored')
            self.assertFalse((root / 'escape').exists())

    def test_production_restore_rejected_before_any_command(self):
        from argparse import Namespace
        with self.assertRaises(ValueError):
            backup.restore(Namespace(database='shop'))

    def test_invalid_database_identifiers(self):
        for name in ['shop; DROP DATABASE shop', '../shop', 'shop`']:
            with self.assertRaises(ValueError):
                backup.validate_database(name)


if __name__ == '__main__':
    unittest.main()
